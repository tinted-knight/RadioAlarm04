package com.noomit.domain.server_manager

import com.noomit.domain.RadioBrowserContract
import com.noomit.domain.ServerInfo
import com.noomit.domain.ServerListResponse
import kotlinx.coroutines.flow.MutableStateFlow

class ServerManager(
    private val apiService: RadioBrowserContract,
) : ServerManagerContract {

    private val _state = MutableStateFlow<ServerState>(ServerState.Loading)
    override val state = _state

    override val activeServer get() = apiService.activeServer

    override suspend fun getAvalilable() {
        _state.value = ServerState.Loading
        when (val serverList = apiService.checkForAvailableServers()) {
            is ServerListResponse.Success -> {
                _state.value = ServerState.Values(serverList.value)
            }
            is ServerListResponse.Failure -> {
                // #todo handle various failure reasons
                _state.value = ServerState.Failure(Exception(serverList.error.toString()))
            }
        }
    }

    override fun setServerManually(serverInfo: ServerInfo) {
        apiService.setActiveServer(serverInfo)
    }
}
