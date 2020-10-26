package com.noomit.radioalarm02.radiobrowserview.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.ServerInfo
import com.noomit.radioalarm02.model.LanguageModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app").i("$message [${Thread.currentThread().name}]")

@ExperimentalCoroutinesApi
class RadioBrowserViewModel(apiService: RadioBrowserService) : ViewModel() {

    private val serverManager = ServerManager(apiService)

    private val languageManager = LanguageManager(apiService)

    private val stationManager = StationManager(
        via = apiService,
        observe = languageManager.chosenCategory,
        scope = viewModelScope,
    )

    val availableServers = serverManager.availableServers

    val languageList = languageManager.values

    val stationList = stationManager.state

    init {
        plog("RadioBrowserViewModel.init")
    }

    fun setServer(serverInfo: ServerInfo) = serverManager.setServer(serverInfo)

    fun onLanguageChoosed(value: LanguageModel) = languageManager.onCategoryChoosed(value)
}
