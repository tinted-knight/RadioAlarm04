package com.noomit.radioalarm02.ui.radio_browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.radiobrowser.ServerInfo
import com.noomit.radioalarm02.data.CategoryModel
import com.noomit.radioalarm02.domain.language_manager.CategoryManager
import com.noomit.radioalarm02.domain.language_manager.CategoryManagerState
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

@FlowPreview
class RadioBrowserViewModel(
    private val serverManager: ServerManager,
    private val categoryManager: CategoryManager,
    private val stationManager: StationManager,
) : ViewModel() {

    val availableServers = serverManager.state

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

    fun applyFilter(name: String?) = viewModelScope.launch {
        filter.emit(name?.toLowerCase(Locale.getDefault()) ?: "")
    }

    // Stations
    fun showStations(category: CategoryModel) = viewModelScope.launch {
        clearFilter()
        stationManager.stationsBy(category)
    }

    /**
     * List of stations by category (i.e. language, tag), filtered by **station name** set up with
     * [applyFilter] method
     */
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
                    emit(StationManagerState.Success(list, state.category))
                }
            } else {
                emit(state)
            }
        }
        .flowOn(Dispatchers.Default)

    // Categories
    fun getTagList() = viewModelScope.launch {
        clearFilter()
        categoryManager.getTags()
    }

    fun getLanguageList() = viewModelScope.launch {
        clearFilter()
        categoryManager.getLanguages()
    }

    /**
     * List of categories(languages or tags), filtered by **category name** set up with
     * [applyFilter] method
     */
    val categoryList: Flow<CategoryManagerState> = filter
        .debounce(500)
        .combineTransform(categoryManager.state) { filter, state ->
            if (state !is CategoryManagerState.Values || filter.isBlank()) {
                emit(state)
            } else {
                val filtered = state.values.filter {
                    it.name.toLowerCase(Locale.getDefault()).contains(filter)
                }
                emit(CategoryManagerState.Values(filtered))
            }
        }

    private fun clearFilter() = applyFilter("")
}
