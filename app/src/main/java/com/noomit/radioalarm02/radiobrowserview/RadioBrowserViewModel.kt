package com.noomit.radioalarm02.radiobrowserview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.ServerListResponse.Failure
import com.example.radiobrowser.ServerListResponse.Success
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app").i("$message [${Thread.currentThread().name}]")

class RadioBrowserViewModel(private val apiService: RadioBrowserService) : ViewModel() {

    val availableServers: LiveData<Result<List<String>>> = liveData() {
        plog("RadioBrowserViewModel")
        when (val serverList = apiService.checkForAvailableServers()) {
            is Success -> {
                plog("Success:")
                serverList.value.onEach { plog("$it") }
                emit(Result.success(serverList.value.map { it.urlString }))
            }
            is Failure -> {
                plog("Failure: ${serverList.error}")
                emit(Result.failure<List<String>>(Exception(serverList.error.toString())))
            }
        }
    }

    fun setServer(id: Int) = apiService.setActiveServer(id)
}
