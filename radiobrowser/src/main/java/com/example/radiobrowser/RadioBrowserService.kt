package com.example.radiobrowser

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress

private fun plog(message: String) = Log.i("tagg", message)

private data class ServerInfo(val name: String, val isReachable: Boolean)

class RadioBrowserService() {

    private val api = RadioBrowserController.getApi()

    private var baseUrl = ""

    init {
        plog("RadioBrowserService::init")
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun getAvailableServers(): Boolean = withContext(Dispatchers.IO) {
        plog("looking for available servers")
        val rawServerList = InetAddress.getAllByName("all.api.radio-browser.info")
        val serverList: List<ServerInfo> =
            rawServerList
                .asList()
                .distinctBy { it.canonicalHostName }
                .map { ServerInfo(name = it.canonicalHostName, isReachable = it.isReachable(500)) }

        val haveReachableServer = serverList.any { it.isReachable }
        if (haveReachableServer) baseUrl = serverList.first { it.isReachable }.name

        return@withContext haveReachableServer
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

    suspend fun stationsByCountry(langString: String): List<StationNetworkEntity> {
        return api.getStationsByLanguage(langString)
    }

    suspend fun stationsByTag(tag: String): List<StationNetworkEntity> {
        return api.getStationsByTag(tag)
    }

    suspend fun search(name: String, tag: String): List<StationNetworkEntity> {
        return api.search(SearchRequest(name, tag))
    }

}