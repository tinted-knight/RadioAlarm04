package com.noomit.radioalarm02.ui.radio_browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiobrowser.ServerInfo
import com.noomit.radioalarm02.data.LanguageModel
import com.noomit.radioalarm02.domain.language_manager.LanguageManager
import com.noomit.radioalarm02.domain.server_manager.ServerManager
import com.noomit.radioalarm02.domain.station_manager.StationManager
import com.noomit.radioalarm02.domain.station_manager.StationManagerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

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

    private val filter = MutableStateFlow("")

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
                clearFilter()
                stationManager.stationsBy(action.value)
            }
            is Action.SetServer -> TODO()
        }
    }

    /**
     * List of stations by category (i.e. language, tag), filtered by **station name** set up with
     * [filterStation] method
     */
    @FlowPreview
    val stationList: Flow<StationManagerState> = filter
        .debounce(500)
        .combineTransform(stationManager.state) { filter, state ->
            if (state is StationManagerState.Success) {
                if (filter.isBlank()) {
                    emit(state)
                } else {
                    val list = state.values.filter {
                        it.name.toLowerCase(Locale.getDefault()).contains(filter)
                    }
                    emit(StationManagerState.Success(list, state.language))
                }
            } else {
                emit(state)
            }
        }
        .flowOn(Dispatchers.Default)

    fun filterStation(name: String?) {
        if (name != null) {
            viewModelScope.launch { filter.emit(name.toLowerCase(Locale.getDefault())) }
        } else {
            viewModelScope.launch { filter.emit("") }
        }
    }

    private fun clearFilter() = filterStation("")
}
