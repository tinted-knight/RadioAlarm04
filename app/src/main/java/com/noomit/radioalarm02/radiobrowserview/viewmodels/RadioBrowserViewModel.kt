package com.noomit.radioalarm02.radiobrowserview.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiobrowser.ServerInfo
import com.noomit.radioalarm02.model.LanguageModel
import com.noomit.radioalarm02.radiobrowserview.viewmodels.categories.LanguageManager
import com.noomit.radioalarm02.radiobrowserview.viewmodels.stations.StationManager
import kotlinx.coroutines.launch
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app").i("$message [${Thread.currentThread().name}]")

enum class Categories { Language, Tag }

sealed class Action {
    sealed class Click : Action() {
        object LanguageList : Click()
        object TagList : Click()
        data class StationsByLanguage(val value: LanguageModel) : Click()
    }

    data class SetServer(val value: ServerInfo) : Action()
}

class RadioBrowserViewModel(
    private val serverManager: ServerManager,
    private val languageManager: LanguageManager,
    private val stationManager: StationManager,
) : ViewModel() {

    val availableServers = serverManager.state

    val languageList = languageManager.state

    val stationList = stationManager.state

    init {
        plog("RadioBrowserViewModel.init")
        serverManager.getAvalilable(viewModelScope)
    }

    override fun onCleared() {
        plog("RadioBrowserViewModel.onCleared")
        super.onCleared()
    }

    fun setServer(serverInfo: ServerInfo) = serverManager.setServerManually(serverInfo)

    fun offer(action: Action) {
        when (action) {
            Action.Click.LanguageList -> viewModelScope.launch { languageManager.getLanguages() }
            Action.Click.TagList -> TODO()
            is Action.Click.StationsByLanguage -> viewModelScope.launch {
                stationManager.stationsBy(action.value)
            }
            is Action.SetServer -> TODO()
        }
    }
}
