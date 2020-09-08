package com.noomit.radioalarm02.radiobrowserview

import androidx.lifecycle.*
import com.bumptech.glide.load.HttpException
import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.ServerListResponse.Failure
import com.example.radiobrowser.ServerListResponse.Success
import com.noomit.radioalarm02.radiobrowserview.model.LanguageModel
import com.noomit.radioalarm02.radiobrowserview.model.StationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app").i("$message [${Thread.currentThread().name}]")

typealias StationList = List<StationModel>

typealias StationListResponse = Result<StationList>

typealias LanguageList = List<LanguageModel>

typealias LanguageListResponse = Result<LanguageList>

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

    val languageList: LiveData<LanguageListResponse> = liveData(Dispatchers.Default) {
        plog("get language list")
        try {
            val languageList = withContext(Dispatchers.IO) { apiService.getLanguageList() }
            if (!languageList.isNullOrEmpty()) {
                // #fake delay
                delay(500)
                val forViewList = languageList.map {
                    LanguageModel(
                        name = it.name,
                        stationCount = it.stationcount.toString(),
                    )
                }
                plog("${forViewList.size}")
                emit(Result.success(forViewList))
            }
        } catch (e: HttpException) {
            plog(e.localizedMessage ?: "Exception: no message")
            emit(Result.failure<LanguageList>(Exception("http exception")))
        }
    }

    private var chosenLanguage = MutableLiveData<LanguageModel>()

    fun onLanguageChoosed(value: LanguageModel) {
        chosenLanguage.value = value
    }

    val stationList: LiveData<StationListResponse> = chosenLanguage.switchMap {
        plog("get station list")
        liveData {
            try {
                val stationList = withContext(Dispatchers.IO) {
                    apiService.getStationsByLanguage(it.name)
                }
            } catch (e: HttpException) {
                plog(e.localizedMessage ?: "Exception: no message")
                emit(Result.failure<StationList>(Exception("http exception")))
            }
        }
    }
}
