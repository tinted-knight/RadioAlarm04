package com.noomit.data.remote

import android.util.Log
import com.noomit.domain.*
import com.noomit.domain.entities.CategoryNetworkEntity
import com.noomit.domain.entities.SearchRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.UnknownHostException

private fun plog(message: String) =
    Log.i("tagg-radio_service", "$message [${Thread.currentThread().name}]")

class RadioBrowserService : RadioBrowserContract {

    init {
        plog("RadioBrowserService::init")
    }

    private lateinit var api: RadioBrowserApi

    //  #todo This is the only usage of Flow here
    //      Probabaly active server logic should be moved to domain,
    //      so we can get rid of Flow in the whole data layer
    private val _activeServer = MutableStateFlow<ActiveServerState>(ActiveServerState.None)
    override val activeServer: StateFlow<ActiveServerState> = _activeServer

    private fun isReachable(addr: String, port: Int, timeout: Int): Boolean {
        return try {
            val socket = Socket()
            socket.connect(InetSocketAddress(addr, port), timeout)
            true
        } catch (e: IOException) {
            false
        }
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun checkForAvailableServers(): ServerListResponse =
        withContext(Dispatchers.IO) {
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
                // #todo "fr" server look less stable in my location, so I try to stick with
                //  "de" or "nl". It may differ in other region, so this "hardcode" decision
                //  is obviously terrible and has to be rethinked / refactored
                serverList.filter { it.urlString.contains("de1") || it.urlString.contains("nl1") }
                    .firstOrNull { it.isReachable }?.let {
                        setActiveServer(it)
                        return@withContext ServerListResponse.Success(serverList)
                    }
                // if not return first available
                serverList.firstOrNull { it.isReachable }?.let {
                    setActiveServer(it)
                    return@withContext ServerListResponse.Success(serverList)
                }

                return@withContext ServerListResponse.Failure(ServerListResponse.ServerListFailure.NoReachableServers)

            } catch (e: UnknownHostException) {
                return@withContext ServerListResponse.Failure(ServerListResponse.ServerListFailure.UnknownHost)

            } catch (e: IOException) {
                return@withContext ServerListResponse.Failure(ServerListResponse.ServerListFailure.NetworkError)
            }
        }

    override fun setActiveServer(serverInfo: ServerInfo) {
        api = getApi("https://${serverInfo.urlString}/json/")
        _activeServer.value = ActiveServerState.Value(serverInfo)
    }

    override suspend fun getLanguageList() = getLanguageListOrThrow()

    override suspend fun getTagList() = api.getTagList()

    override suspend fun stationsByLanguage(langString: String) =
        api.getStationsByLanguage(langString)

    override suspend fun stationsByTag(tagString: String) = api.getStationsByTag(tagString)

    override suspend fun getAllStations() = api.getAllStations()

    override suspend fun getTopVoted() = api.getTopVoted()

    private suspend fun getLanguageListOrThrow(): List<CategoryNetworkEntity> {
        // #todo wrapper for every method that calls [api] field
        if (::api.isInitialized) {
            return api.getLanguageList()
        } else {
            throw UninitializedPropertyAccessException("lateinit errrrror")
        }
    }

    override suspend fun search(name: String, tag: String) = api.search(SearchRequest(name, tag))
}
