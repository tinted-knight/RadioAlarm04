package com.noomit.radioalarm02.domain.station_manager

import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.StationNetworkEntity
import com.noomit.radioalarm02.base.WithLogTag
import com.noomit.radioalarm02.data.CategoryModel
import com.noomit.radioalarm02.data.StationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

typealias StationList = List<StationModel>

class StationManager(via: RadioBrowserService) : WithLogTag {

    override val logTag = "station_manager"

    private val apiService = via

    private val _state = MutableStateFlow<StationManagerState>(StationManagerState.Loading)
    val state: StateFlow<StationManagerState> = _state

    suspend fun stationsBy(category: CategoryModel) {
        val last = state.value
        if (last is StationManagerState.Success && last.category == category) {
            return
        }

        _state.value = StationManagerState.Loading

        val flow: Flow<List<StationNetworkEntity>> = when (category) {
            is CategoryModel.Language -> apiService.stationsByLanguage(category.name)
            is CategoryModel.Tag -> apiService.stationsByTag(category.name)
            is CategoryModel.TopVoted -> flowOf(apiService.getTopVoted())
        }

        flow.flowOn(Dispatchers.IO)
            // #fake delay
            .onEach { delay(250) }
            .map { stationList ->
                stationList.sortedByDescending { it.votes }
                    .map {
                        val tagList = it.tags.split(",").onEach { tag -> tag.trim() }
                        StationModel(
                            name = it.name,
                            upvotes = it.votes.toString(),
                            streamUrl = it.url,
                            country = it.country,
                            homepage = it.homepage,
                            codec = it.codec,
                            bitrate = it.bitrate,
                            favicon = it.favicon,
                            tags = tagList,
                        )
                    }
            }
            .flowOn(Dispatchers.Default)
            .catch { e -> _state.value = StationManagerState.Failure(e) }
            .collect { _state.value = StationManagerState.Success(it, category) }
    }
}
