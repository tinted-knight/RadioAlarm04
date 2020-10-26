package com.noomit.radioalarm02.radiobrowserview.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.ServerInfo
import com.noomit.radioalarm02.model.LanguageModel
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app").i("$message [${Thread.currentThread().name}]")

class RadioBrowserViewModel(apiService: RadioBrowserService) : ViewModel() {

    private val serverManager = ServerManager(apiService)

    private val languageManager = LanguageManager(apiService)

    private val stationManager = StationManager(
        via = apiService,
        observe = languageManager.chosenCategory
    )

    init {
        plog("RadioBrowserViewModel.init")
    }

    val availableServers = serverManager.availableServers

    val languageList = languageManager.values

    val stationList = stationManager.values

    fun setServer(serverInfo: ServerInfo) = serverManager.setServer(serverInfo)

    fun onLanguageChoosed(value: LanguageModel) = languageManager.onCategoryChoosed(value)

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
