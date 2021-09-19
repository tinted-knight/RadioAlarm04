package com.noomit.data.remote

import com.noomit.domain.radio_browser.ServerResolver
import com.noomit.domain.radio_browser.ServerInfo
import com.noomit.domain.radio_browser.ServerListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.UnknownHostException
import javax.inject.Inject

class ServerResolverImpl @Inject constructor() : ServerResolver {

    override val cached = MutableStateFlow<ServerListResponse>(ServerListResponse.Loading)

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun checkAlive() = withContext(Dispatchers.IO) {
        try {
            val rawServerList = InetAddress.getAllByName("all.api.radio-browser.info")
            val serverList = mutableListOf<ServerInfo>()
            serverList.addAll(rawServerList
                .asList()
                .distinctBy { it.canonicalHostName }
                .map {
                    ServerInfo(
                        urlString = it.canonicalHostName,
                        isReachable = isReachable(it.canonicalHostName, 80, 500)
                    )
                }
            )

            cached.emit(
                if (serverList.isNotEmpty()) {
                    ServerListResponse.Success(serverList)
                } else {
                    ServerListResponse.Failure(ServerListResponse.ServerListFailure.NoReachableServers)
                }
            )

        } catch (e: UnknownHostException) {
            cached.emit(ServerListResponse.Failure(ServerListResponse.ServerListFailure.UnknownHost))

        } catch (e: IOException) {
            cached.emit(ServerListResponse.Failure(ServerListResponse.ServerListFailure.NetworkError))
        }
    }

    private fun isReachable(addr: String, port: Int, timeout: Int): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(addr, port), timeout)
            true
        } catch (e: IOException) {
            false
        }
    }
}