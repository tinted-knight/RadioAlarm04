package com.noomit.radioalarm02.radiobrowserview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.bumptech.glide.load.HttpException
import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.ServerListResponse.Failure
import com.example.radiobrowser.ServerListResponse.Success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app").i("$message [${Thread.currentThread().name}]")

class RadioBrowserViewModel(private val apiService: RadioBrowserService) : ViewModel() {

    val availableServers: LiveData<Result<List<String>>> = liveData(Dispatchers.Default) {
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

    fun setServer(id: Int) {
        apiService.setActiveServer(id)
    }

    val languageList: LiveData<Result<List<String>>> = liveData(Dispatchers.Default) {
        plog("get language list")
        try {
            val languageList = withContext(Dispatchers.IO) { apiService.getLanguageList() }
            if (!languageList.isNullOrEmpty()) {
                val forViewList = languageList.map {
                    plog(it.name)
                    it.name
                }
                emit(Result.success(forViewList))
            }
        } catch (e: HttpException) {
            plog(e.localizedMessage ?: "Exception: no message")
            emit(Result.failure<List<String>>(Exception("http exception")))
        }
    }
}
