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
import java.util.*

@FlowPreview
class RadioBrowserViewModel(
    private val serverManager: ServerManager,
    private val categoryManager: CategoryManager,
    private val stationManager: StationManager,
) : ViewModel() {

    val availableServers = serverManager.state

    val activeServer = serverManager.activeServer

    private val caterogyFilter = MutableStateFlow<Filter>(Filter.None)

    private val stationFilter = MutableStateFlow<Filter>(Filter.None)

    init {
        serverManager.getAvalilable(viewModelScope)
    }

    fun setServer(serverInfo: ServerInfo) = serverManager.setServerManually(serverInfo)

    fun applyCategoryFilter(stringFlow: Flow<String?>) = viewModelScope.launch {
        stringFlow.filterNotNull().collect {
            if (it.isBlank()) {
                caterogyFilter.emit(Filter.None)
            } else {
                caterogyFilter.emit(Filter.Value(it))
            }
        }
    }

    fun applyStationFilter(stringFlow: Flow<String?>) = viewModelScope.launch {
        stringFlow.collect {
            if (it.isNullOrBlank()) {
                stationFilter.emit(Filter.None)
            } else {
                stationFilter.emit(Filter.Value(it))
            }
        }
    }

    /**
     * Request stations by [category]. Observe result by [stationList]
     */
    fun showStations(category: CategoryModel) = viewModelScope.launch {
        clearStationFilter()
        stationManager.stationsBy(category)
    }

    /**
     * List of stations by category (i.e. language, tag), filtered by **station name** set up with
     * [applyCategoryFilter] method
     */
    val stationList: Flow<StationManagerState> = stationFilter
        .combineTransform(stationManager.state) { filter, state ->
            if (state !is StationManagerState.Success || filter !is Filter.Value) {
                emit(state)
            } else {
                val list = state.values.filter {
                    it.name.toLowerCase(Locale.getDefault()).contains(filter.value)
                }
                emit(StationManagerState.Success(list, state.category))
            }
        }
        .flowOn(Dispatchers.Default)

    // Categories
    fun getTagList() = viewModelScope.launch {
        clearCategoryFilter()
        categoryManager.getTags()
    }

    fun getLanguageList() = viewModelScope.launch {
        clearCategoryFilter()
        categoryManager.getLanguages()
    }

    /**
     * List of categories(languages or tags), filtered by **category name** set up with
     * [applyCategoryFilter] method
     */
    val categoryList: Flow<CategoryManagerState> = caterogyFilter
        .combineTransform(categoryManager.state) { filter, state ->
            if (state !is CategoryManagerState.Values || filter !is Filter.Value) {
                emit(state)
            } else {
                val filtered = state.values.filter {
                    it.name.toLowerCase(Locale.getDefault()).contains(filter.value)
                }
                emit(CategoryManagerState.Values(filtered))
            }
        }

    private fun clearCategoryFilter() = viewModelScope.launch { caterogyFilter.emit(Filter.None) }

    private fun clearStationFilter() = viewModelScope.launch { stationFilter.emit(Filter.None) }
}

private sealed class Filter {
    object None : Filter()
    data class Value(val value: String) : Filter()
}
