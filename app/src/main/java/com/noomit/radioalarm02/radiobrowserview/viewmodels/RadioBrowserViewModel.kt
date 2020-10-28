package com.noomit.radioalarm02.radiobrowserview.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.ServerInfo
import com.noomit.radioalarm02.model.LanguageModel
import com.noomit.radioalarm02.radiobrowserview.viewmodels.categories.LanguageManager
import com.noomit.radioalarm02.radiobrowserview.viewmodels.stations.StationManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app").i("$message [${Thread.currentThread().name}]")

enum class Categories { Language, Tag }

sealed class Action {
    object Idle : Action()

    sealed class Show : Action() {
        object LanguageList : Show()
        object TagList : Show()
        data class StationsByLanguage(val value: LanguageModel) : Show()
    }

    data class SetServer(val value: ServerInfo) : Action()
}

@ExperimentalCoroutinesApi
class RadioBrowserViewModel(apiService: RadioBrowserService) : ViewModel() {

    private val actionFlow = MutableStateFlow<Action>(Action.Idle)

    private val serverManager = ServerManager(
        apiService = apiService,
        scope = viewModelScope,
    )

    private val languageManager = LanguageManager(
        apiService = apiService,
        actions = actionFlow as Flow<Action>,
        scope = viewModelScope,
    )

    private val stationManager = StationManager(
        via = apiService,
        watchFor = actionFlow as Flow<Action>,
        scope = viewModelScope,
    )

    val availableServers = serverManager.state

    val languageList = languageManager.state

    val stationList = stationManager.state

    init {
        plog("RadioBrowserViewModel.init")
    }

    override fun onCleared() {
        plog("RadioBrowserViewModel.onCleared")
        super.onCleared()
    }

    fun setServer(serverInfo: ServerInfo) = serverManager.setServerManually(serverInfo)

    fun offer(action: Action) {
        actionFlow.value = action
    }
}
