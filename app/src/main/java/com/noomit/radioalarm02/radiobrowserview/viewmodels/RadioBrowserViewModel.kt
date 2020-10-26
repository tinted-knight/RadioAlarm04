package com.noomit.radioalarm02.radiobrowserview.viewmodels

import androidx.lifecycle.*
import com.bumptech.glide.load.HttpException
import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.ServerInfo
import com.example.radiobrowser.ServerListResponse.Failure
import com.example.radiobrowser.ServerListResponse.Success
import com.noomit.radioalarm02.model.LanguageModel
import com.noomit.radioalarm02.model.StationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app").i("$message [${Thread.currentThread().name}]")

typealias StationList = List<StationModel>

typealias StationListResponse = Result<StationList>

class RadioBrowserViewModel(private val apiService: RadioBrowserService) : ViewModel() {

    private val languageManager = LanguageManager(apiService)

    init {
        plog("RadioBrowserViewModel.init")
    }

    val availableServers: LiveData<Result<List<ServerInfo>>> = liveData(Dispatchers.IO) {
        plog("RadioBrowserViewModel")
        when (val serverList = apiService.checkForAvailableServers()) {
            is Success -> {
                plog("Success:")
                serverList.value.onEach { plog("$it") }
                emit(Result.success(serverList.value))
            }
            is Failure -> {
                plog("Failure: ${serverList.error}")
                // #todo handle various failure reasons
                emit(Result.failure<List<ServerInfo>>(Exception(serverList.error.toString())))
            }
        }
    }

    fun setServer(serverInfo: ServerInfo) {
        apiService.setActiveServer(serverInfo)
    }

    val languageList = languageManager.languageFlow.asLiveData()

    fun onLanguageChoosed(value: LanguageModel) = languageManager.onLanguageChoosed(value)

//    val stationFlow: Flow<StationListResponse> = languageManager.langFlow
//        .map { apiService.getStationsByLanguage(it.name) }
//        .flowOn(Dispatchers.IO)
//        .catch { Result.failure<StationList>(JavaLangException("htto exception")) }
//        .filterNot { it.isNullOrEmpty() }
//        .map { stations ->
//            Result.success(stations.sortedByDescending { it.votes }
//                .map {
//                    StationModel(
//                        name = it.name,
//                        upvotes = it.votes.toString(),
//                        streamUrl = it.url,
//                        country = it.country,
//                        homepage = it.homepage,
//                        codec = it.codec,
//                        bitrate = it.bitrate,
//                        favicon = it.favicon,
//                        tags = it.tags
//                    )
//                }
//            )
//        }
//        .flowOn(Dispatchers.Default)

    val stationList: LiveData<StationListResponse> = languageManager.chosenLanguage.switchMap {
        plog("get station list")
        liveData {
            try {
                emit(Result.success(emptyList()))
                val stationList = withContext(Dispatchers.IO) {
                    apiService.getStationsByLanguage(it.name)
                }
                // #think
                if (stationList.isNullOrEmpty()) {
                    emit(Result.success(emptyList<StationModel>()))
                } else {
                    // #fake delay
                    delay(500)
                    val forViewList = stationList
                        .sortedByDescending { it.votes }
                        .map {
                            StationModel(
                                name = it.name,
                                upvotes = it.votes.toString(),
                                streamUrl = it.url,
                                country = it.country,
                                homepage = it.homepage,
                                codec = it.codec,
                                bitrate = it.bitrate,
                                favicon = it.favicon,
                                tags = it.tags,
                            )
                        }
                    plog("${forViewList.size}")
                    emit(Result.success(forViewList))
                }
            } catch (e: HttpException) {
                plog(e.localizedMessage ?: "Exception: no message")
                emit(Result.failure<StationList>(Exception("http exception")))
            }
        }
    }

    // #future
    val filteredStationList: LiveData<StationListResponse> = stationList.switchMap { response ->
        liveData {
            response.fold(
                onSuccess = {},
                onFailure = { emit(Result.failure<StationList>(it)) },
            )
        }
    }
}
