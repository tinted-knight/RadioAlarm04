package com.example.radiobrowser

import android.util.Log
import com.example.radiobrowser.ServerListResponse.ServerListFailure
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
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

sealed class ActiveServerState {
    object None : ActiveServerState()
    data class Value(val serverInfo: ServerInfo) : ActiveServerState()
}

@ExperimentalCoroutinesApi
class RadioBrowserService() {

    private lateinit var api: RadioBrowserApi

    private val _activeServer = MutableStateFlow<ActiveServerState>(ActiveServerState.None)
    val activeServer: StateFlow<ActiveServerState> = _activeServer

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

            serverList.firstOrNull { it.isReachable }?.let {
                setActiveServer(it)
                return@withContext ServerListResponse.Success(serverList)
            }

            return@withContext ServerListResponse.Failure(ServerListFailure.NoReachableServers)

        } catch (e: UnknownHostException) {
            plog("RadioBrowserService.UnknownHostException")
            return@withContext ServerListResponse.Failure(ServerListFailure.UnknownHost)

        } catch (e: IOException) {
            plog("RadioBrowserService.IOException")
            return@withContext ServerListResponse.Failure(ServerListFailure.NetworkError)
        }
    }

    fun setActiveServer(serverInfo: ServerInfo) {
        api = getApi("https://${serverInfo.urlString}/json/")
        plog("setActiveServer: ${serverInfo.urlString}")
        _activeServer.value = ActiveServerState.Value(serverInfo)
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
        plog("getLanguageList")
        // #todo wrapper for every method that calls [api] field
        if (::api.isInitialized) {
            return api.getLanguageList()
        } else {
            throw UninitializedPropertyAccessException("lateinit errrrror")
        }
    }

    fun getLanguageListFlow(): Flow<List<LanguageNetworkEntity>> = flow { emit(getLanguageList()) }

    suspend fun getStationsByLanguage(langString: String): List<StationNetworkEntity> {
        return api.getStationsByLanguage(langString)
    }

    fun stationsByLanguage(langString: String) = flow { emit(getStationsByLanguage(langString)) }

    suspend fun getStationsByTag(tag: String): List<StationNetworkEntity> {
        return api.getStationsByTag(tag)
    }

    suspend fun search(name: String, tag: String): List<StationNetworkEntity> {
        return api.search(SearchRequest(name, tag))
    }

}
