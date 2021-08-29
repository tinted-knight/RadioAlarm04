package com.noomit.data.remote

import com.noomit.domain.*
import com.noomit.domain.entities.CategoryNetworkEntity
import com.noomit.domain.entities.SearchRequest
import com.noomit.domain.radio_browser.ActiveServerState
import com.noomit.domain.radio_browser.RadioBrowser
import com.noomit.domain.radio_browser.ServerInfo
import com.noomit.domain.radio_browser.ServerListResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.net.UnknownHostException
import javax.inject.Inject

class RadioBrowserImpl @Inject constructor(
    private val apiFactory: ApiFactoryContract
) : RadioBrowser {

    private var api: RadioBrowserApi? = null

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
    override suspend fun checkForAvailableServers(): ServerListResponse = withContext(Dispatchers.IO) {
        try {
            val rawServerList = InetAddress.getAllByName("all.api.radio-browser.info")
            val serverList = mutableListOf<ServerInfo>()
            //  #todo   should not call isReachable here,
            //          cause there may be long list of mirrors,
            //          so it is better to filter first
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
        api = apiFactory.get("https://${serverInfo.urlString}/json/")
        _activeServer.value = ActiveServerState.Value(serverInfo)
    }

    override suspend fun getLanguageList() = getOrThrow { getLanguageList() }

    override suspend fun getTagList() = getOrThrow { getTagList() }

    override suspend fun stationsByLanguage(langString: String) =
        getOrThrow { getStationsByLanguage(langString) }

    override suspend fun stationsByTag(tagString: String) = getOrThrow { getStationsByTag(tagString) }

    override suspend fun getAllStations() = getOrThrow { getAllStations() }

    override suspend fun getTopVoted() = getOrThrow { getTopVoted() }

    override suspend fun search(name: String, tag: String) = getOrThrow { search(SearchRequest(name, tag)) }

    private suspend fun <T> getOrThrow(block: suspend RadioBrowserApi.() -> List<T>): List<T> {
        val api = api ?: throw UninitializedPropertyAccessException("API service not initialized")
        return block(api)
    }
}
