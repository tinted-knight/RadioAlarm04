package com.noomit.domain.server_manager

import com.noomit.domain.radio_browser.ActiveServerState
import com.noomit.domain.radio_browser.ServerInfo
import kotlinx.coroutines.flow.Flow

interface ServerManager {
    val state: Flow<ServerState>
    fun activeServer(): Flow<ActiveServerState>

    suspend fun getAvalilable()
    fun setServerManually(serverInfo: ServerInfo)
}

sealed class ServerState {
    object Loading : ServerState()
    data class Values(val values: List<ServerInfo>) : ServerState()
    data class Failure(val e: Throwable) : ServerState()
}
