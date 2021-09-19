package com.noomit.domain.server_manager

import com.noomit.domain.radio_browser.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class ServerManagerImpl @Inject constructor(
    private val serverResolver: ServerResolver,
) : ServerManager {

    override val state = MutableStateFlow<ServerState>(ServerState.Loading)

    override fun activeServer(): Flow<ActiveServerState> = flow {
        serverResolver.cached
            .collect { serverList ->
                when (serverList) {
                    is ServerListResponse.Loading -> {
                        state.value = ServerState.Loading
                        emit(ActiveServerState.Loading)
                    }
                    is ServerListResponse.Success -> {
                        val server = serverList.value
                            .firstOrNull() { it.urlString.contains("de1") || it.urlString.contains("nl1") }
                            ?: serverList.value.first()

                        state.value = ServerState.Values(serverList.value)
                        emit(ActiveServerState.Value(server))
                    }
                    is ServerListResponse.Failure -> {
                        // #todo handle various failure reasons
                        state.value = ServerState.Failure(Exception(serverList.error.toString()))
                        emit(ActiveServerState.None)
                    }
                }
            }
    }

    override suspend fun getAvalilable() = serverResolver.checkAlive()

    override fun setServerManually(serverInfo: ServerInfo) {
//        apiService.setActiveServer(serverInfo)
    }
}
