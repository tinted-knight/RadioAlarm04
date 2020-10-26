package com.noomit.radioalarm02.radiobrowserview.viewmodels

import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.ServerInfo
import com.example.radiobrowser.ServerListResponse
import com.noomit.radioalarm02.base.WithLogTag
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ServerState {
    object Loading : ServerState()
    data class Values(val values: List<ServerInfo>) : ServerState()
    data class Failure(val e: Throwable) : ServerState()
}

@ExperimentalCoroutinesApi
class ServerManager(
    private val apiService: RadioBrowserService,
    scope: CoroutineScope,
) : WithLogTag {
    override val logTag = "tagg-app-servers"

    private val _state = MutableStateFlow<ServerState>(ServerState.Loading)
    val state: StateFlow<ServerState> = _state

    init {
        scope.launch(Dispatchers.IO) {
            plog("RadioBrowserViewModel")
            when (val serverList = apiService.checkForAvailableServers()) {
                is ServerListResponse.Success -> {
                    plog("Success:")
                    serverList.value.onEach { plog("$it") }
                    _state.value = ServerState.Values(serverList.value)
                }
                is ServerListResponse.Failure -> {
                    plog("Failure: ${serverList.error}")
                    // #todo handle various failure reasons
                    _state.value = ServerState.Failure(Exception(serverList.error.toString()))
                }
            }
        }
    }

    fun setServerManually(serverInfo: ServerInfo) {
        apiService.setActiveServer(serverInfo)
    }
}
