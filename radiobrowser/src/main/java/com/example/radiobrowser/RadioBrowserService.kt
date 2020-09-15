package com.example.radiobrowser

import android.util.Log
import com.example.radiobrowser.ServerListResponse.ServerListFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.UnknownHostException

private fun plog(message: String) =
    Log.i("tagg-radio_service", "$message [${Thread.currentThread().name}]")

data class ServerInfo(val urlString: String, val isReachable: Boolean)

sealed class ServerListResponse {
    sealed class ServerListFailure {
        object UnknownHost : ServerListFailure()
        object NetworkError : ServerListFailure()
        object NoReachableServers : ServerListFailure()
    }

    class Success(val value: List<ServerInfo>) : ServerListResponse()
    class Failure(val error: ServerListFailure) : ServerListResponse()
}

class RadioBrowserService() {

    private lateinit var api: RadioBrowserApi
    private val serverList = mutableListOf<ServerInfo>()

    init {
        plog("RadioBrowserService::init")
    }

    private fun isReachable(addr: String, port: Int, timeout: Int): Boolean {
        try {
            val socket = Socket()
            socket.connect(InetSocketAddress(addr, port), timeout)
            return true;
        } catch (e: IOException) {
            return false
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun checkForAvailableServers(): ServerListResponse = withContext(Dispatchers.IO) {
        plog("looking for available servers")
        try {
            val rawServerList = InetAddress.getAllByName("all.api.radio-browser.info")
            serverList.clear()
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

            val haveReachableServer = serverList.any { it.isReachable }

            return@withContext when {
                haveReachableServer -> {
                    ServerListResponse.Success(serverList)
                }
                else -> ServerListResponse.Failure(ServerListFailure.NoReachableServers)
            }
        } catch (e: UnknownHostException) {
            plog("RadioBrowserService.UnknownHostException")
            return@withContext ServerListResponse.Failure(ServerListFailure.UnknownHost)
        } catch (e: IOException) {
            plog("RadioBrowserService.IOException")
            return@withContext ServerListResponse.Failure(ServerListFailure.NetworkError)
        }
    }

    fun setActiveServer(id: Int) {
        if (!serverList.isNullOrEmpty()) {
            api = getApi("https://${serverList[id].urlString}/json/")
            plog("setActiveServer: ${serverList[id].urlString}")
        }
    }

    suspend fun getAllStations(): List<StationNetworkEntity> {
        return api.getAllStations()
    }

    suspend fun getTopVote(): List<StationNetworkEntity> {
        return api.getTopVoted()
    }

    suspend fun getTags(): List<LanguageNetworkEntity> {
        return api.getTagList()
    }

    suspend fun getLanguageList(): List<LanguageNetworkEntity> {
        return api.getLanguageList()
    }

    suspend fun getStationsByLanguage(langString: String): List<StationNetworkEntity> {
        return api.getStationsByLanguage(langString)
    }

    suspend fun getStationsByTag(tag: String): List<StationNetworkEntity> {
        return api.getStationsByTag(tag)
    }

    suspend fun search(name: String, tag: String): List<StationNetworkEntity> {
        return api.search(SearchRequest(name, tag))
    }

}