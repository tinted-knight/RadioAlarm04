package com.noomit.data.remote

import com.noomit.domain.entities.SearchRequest
import com.noomit.domain.radio_browser.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import javax.inject.Inject

class RadioBrowserImpl @Inject constructor(
    private val apiFactory: ApiFactoryContract,
) : RadioBrowser {

    private var api: RadioBrowserApi? = null

    override fun setActiveServer(serverInfo: ServerInfo) {
        api = apiFactory.get("https://${serverInfo.urlString}/json/")
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
