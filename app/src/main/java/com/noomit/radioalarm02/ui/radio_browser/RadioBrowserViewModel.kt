package com.noomit.radioalarm02.ui.radio_browser

import androidx.lifecycle.viewModelScope
import com.noomit.domain.ActiveServerState
import com.noomit.domain.ServerInfo
import com.noomit.domain.category_manager.CategoryManagerContract
import com.noomit.domain.category_manager.CategoryManagerState
import com.noomit.domain.entities.CategoryModel
import com.noomit.domain.server_manager.ServerManagerContract
import com.noomit.domain.station_manager.StationManagerContract
import com.noomit.domain.station_manager.StationManagerState
import com.noomit.radioalarm02.ui.navigation.NavigationViewModel
import com.noomit.radioalarm02.ui.navigation.OneShotEvent
import com.noomit.radioalarm02.ui.radio_browser.home.RadioBrowserHomeDelegate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class RadioBrowserEvent : OneShotEvent {
    object LanguageList : RadioBrowserEvent()
    object TagList : RadioBrowserEvent()
    object TopVoted : RadioBrowserEvent()
    object Search : RadioBrowserEvent()
}

@FlowPreview
@HiltViewModel
class RadioBrowserViewModel @Inject constructor(
    private val serverManager: ServerManagerContract,
    private val categoryManager: CategoryManagerContract,
    private val stationManager: StationManagerContract,
) : NavigationViewModel<RadioBrowserEvent>(), RadioBrowserHomeDelegate {

    val availableServers = serverManager.state

    private val activeServer = serverManager.activeServer

    private val caterogyFilter = MutableStateFlow<Filter>(Filter.None)

    private val stationFilter = MutableStateFlow<Filter>(Filter.None)

    init {
        viewModelScope.launch {
            // If there is no activeServer, it means something has gone wrong with connection
            // So why not to try once more
            activeServer.collect {
                if (it is ActiveServerState.None) serverManager.getAvalilable()
            }
        }
    }

    fun setServer(serverInfo: ServerInfo) = serverManager.setServerManually(serverInfo)

    fun applyCategoryFilter(stringFlow: Flow<String?>) = viewModelScope.launch {
        stringFlow.debounce(500)
            .filterNotNull()
            .collect {
                if (it.isBlank()) {
                    caterogyFilter.emit(Filter.None)
                } else {
                    caterogyFilter.emit(Filter.Value(it))
                }
            }
    }

    fun applyStationFilter(stringFlow: Flow<String?>) = viewModelScope.launch {
        stringFlow.debounce(500)
            .filterNotNull()
            .collect {
                if (it.isBlank()) {
                    stationFilter.emit(Filter.None)
                } else {
                    stationFilter.emit(Filter.Value(it))
                }
            }
    }

    fun requestCategory(model: CategoryModel) {
        loadStations(model)
    }

    /**
     * Request stations by [category]. Observe result by [stationList]
     */
    private fun loadStations(category: CategoryModel) = viewModelScope.launch {
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
                    it.name.lowercase().contains(filter.value)
                }
                emit(StationManagerState.Success(list, state.category))
            }
        }
        .flowOn(Dispatchers.Default)

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
                    it.name.lowercase().contains(filter.value)
                }
                emit(CategoryManagerState.Values(filtered))
            }
        }

    private fun clearCategoryFilter() = viewModelScope.launch { caterogyFilter.emit(Filter.None) }

    private fun clearStationFilter() = viewModelScope.launch { stationFilter.emit(Filter.None) }

    override fun onLanguageClick() {
        viewModelScope.launch {
            clearCategoryFilter()
            categoryManager.getLanguages()
        }
        navigateTo(RadioBrowserEvent.LanguageList)
    }

    override fun onTagClick() {
        viewModelScope.launch {
            clearCategoryFilter()
            categoryManager.getTags()
        }
        navigateTo(RadioBrowserEvent.TagList)
    }

    override fun onTopVotedClick() {
        loadStations(CategoryModel.TopVoted())
        navigateTo(RadioBrowserEvent.TopVoted)
    }

    data class SearchState(
        val name: String = "",
        val tag: String = "",
    ) {
        val isValid: Boolean
            get() = name.isNotBlank() || tag.isNotBlank()
    }

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState

    override fun onSearchClick() {
        loadStations(CategoryModel.GlobalSearch(
            searchName = _searchState.value.name.lowercase(),
            searchTag = _searchState.value.tag.lowercase()
        ))
        navigateTo(RadioBrowserEvent.Search)
    }

    override fun onSearchNameChanged(value: String?) {
        _searchState.value = _searchState.value.copy(name = value ?: "")
    }

    override fun onSearchTagChanged(value: String?) {
        _searchState.value = _searchState.value.copy(tag = value ?: "")
    }
}

private sealed class Filter {
    object None : Filter()
    data class Value(val value: String) : Filter()
}
