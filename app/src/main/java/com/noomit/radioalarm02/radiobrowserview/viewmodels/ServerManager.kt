package com.noomit.radioalarm02.radiobrowserview.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.ServerInfo
import com.example.radiobrowser.ServerListResponse
import kotlinx.coroutines.Dispatchers

class ServerManager(private val apiService: RadioBrowserService) : WithLogTag {
    override val logTag = "tagg-app-servers"

    val availableServers: LiveData<Result<List<ServerInfo>>> = liveData(Dispatchers.IO) {
        plog("RadioBrowserViewModel")
        when (val serverList = apiService.checkForAvailableServers()) {
            is ServerListResponse.Success -> {
                plog("Success:")
                serverList.value.onEach { plog("$it") }
                emit(Result.success(serverList.value))
            }
            is ServerListResponse.Failure -> {
                plog("Failure: ${serverList.error}")
                // #todo handle various failure reasons
                emit(Result.failure<List<ServerInfo>>(Exception(serverList.error.toString())))
            }
        }
    }

    fun setServer(serverInfo: ServerInfo) {
        apiService.setActiveServer(serverInfo)
    }
}
