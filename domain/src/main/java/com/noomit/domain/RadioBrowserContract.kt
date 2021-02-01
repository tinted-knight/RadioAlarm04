package com.noomit.domain

import kotlinx.coroutines.flow.Flow

data class ServerInfo(val urlString: String, val isReachable: Boolean)

interface RadioBrowserContract {
    val activeServer: Flow<ActiveServerState>
    suspend fun checkForAvailableServers(): ServerListResponse
    fun setActiveServer(serverInfo: ServerInfo)
    suspend fun getLanguageList(): List<CategoryNetworkEntity>
    suspend fun getTagList(): List<CategoryNetworkEntity>
    suspend fun stationsByLanguage(langString: String): List<StationNetworkEntity>
    suspend fun stationsByTag(tagString: String): List<StationNetworkEntity>
    suspend fun getAllStations(): List<StationNetworkEntity>
    suspend fun getTopVoted(): List<StationNetworkEntity>
    suspend fun search(name: String, tag: String): List<StationNetworkEntity>
}

sealed class ActiveServerState {
    object None : ActiveServerState()
    data class Value(val serverInfo: ServerInfo) : ActiveServerState()
}

sealed class ServerListResponse {
    sealed class ServerListFailure {
        object UnknownHost : ServerListFailure()
        object NetworkError : ServerListFailure()
        object NoReachableServers : ServerListFailure()
    }

    class Success(val value: List<ServerInfo>) : ServerListResponse()
    class Failure(val error: ServerListFailure) : ServerListResponse()
}
