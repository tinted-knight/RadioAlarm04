package com.noomit.radioalarm02.ui.radio_browser

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.example.radiobrowser.ServerInfo
import com.noomit.radioalarm02.data.CategoryModel
import com.noomit.radioalarm02.domain.language_manager.CategoryManager
import com.noomit.radioalarm02.domain.language_manager.CategoryManagerState
import com.noomit.radioalarm02.domain.server_manager.ServerManager
import com.noomit.radioalarm02.domain.station_manager.StationManager
import com.noomit.radioalarm02.domain.station_manager.StationManagerState
import com.noomit.radioalarm02.tplog
import com.noomit.radioalarm02.ui.navigation.NavCommand
import com.noomit.radioalarm02.ui.navigation.NavigationViewModel
import com.noomit.radioalarm02.ui.radio_browser.home.RadioBrowserHomeDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

sealed class RadioBrowserDirections : NavCommand {
    object LanguageList : RadioBrowserDirections()
    object TagList : RadioBrowserDirections()
    object TopVoted : RadioBrowserDirections()
    object Search : RadioBrowserDirections()
}

@FlowPreview
class RadioBrowserViewModel @ViewModelInject constructor(
    private val serverManager: ServerManager,
    private val categoryManager: CategoryManager,
    private val stationManager: StationManager,
) : NavigationViewModel<RadioBrowserDirections>(), RadioBrowserHomeDelegate {

    val availableServers = serverManager.state

    val activeServer = serverManager.activeServer

    private val caterogyFilter = MutableStateFlow<Filter>(Filter.None)

    private val stationFilter = MutableStateFlow<Filter>(Filter.None)

    init {
        serverManager.getAvalilable(viewModelScope)
        tplog("RadioBrowserViewModel::init")
    }

    override fun onCleared() {
        tplog("RadioBrowserViewModel::onCleared")
        super.onCleared()
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
                    it.name.toLowerCase(Locale.getDefault()).contains(filter.value)
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
                    it.name.toLowerCase(Locale.getDefault()).contains(filter.value)
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
        navigateTo(RadioBrowserDirections.LanguageList)
    }

    override fun onTagClick() {
        viewModelScope.launch {
            clearCategoryFilter()
            categoryManager.getTags()
        }
        navigateTo(RadioBrowserDirections.TagList)
    }

    override fun onTopVotedClick() {
        loadStations(CategoryModel.TopVoted())
        navigateTo(RadioBrowserDirections.TopVoted)
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
            searchName = _searchState.value.name.toLowerCase(Locale.getDefault()),
            searchTag = _searchState.value.tag.toLowerCase(Locale.getDefault())
        ))
        navigateTo(RadioBrowserDirections.Search)
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
