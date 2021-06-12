package com.noomit.domain.server_manager

import com.noomit.domain.ActiveServerState
import com.noomit.domain.ServerInfo
import kotlinx.coroutines.flow.Flow

interface ServerManagerContract {
    val state: Flow<ServerState>
    val activeServer: Flow<ActiveServerState>
    suspend fun getAvalilable()
    fun setServerManually(serverInfo: ServerInfo)
}

sealed class ServerState {
    object Loading : ServerState()
    data class Values(val values: List<ServerInfo>) : ServerState()
    data class Failure(val e: Throwable) : ServerState()
}
