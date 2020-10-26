package com.noomit.radioalarm02.radiobrowserview.viewmodels

import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.model.StationModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

typealias StationList = List<StationModel>

typealias StationListResponse = Result<StationList>

sealed class StationListState {
    object Loading : StationListState()
    data class Success(val values: StationList) : StationListState()
    data class Failure(val error: Throwable) : StationListState()
}

@ExperimentalCoroutinesApi
class StationManager(
    via: RadioBrowserService,
    observe: Flow<ChosedLanguage>,
    scope: CoroutineScope,
) : WithLogTag {

    override val logTag = "station_manager"

    private val apiService = via

    private val _state = MutableStateFlow<StationListState>(StationListState.Loading)
    val state: StateFlow<StationListState> = _state

    init {
        scope.launch {
            observe
                .onEach { _state.value = StationListState.Loading }
                .onEach { plog(it.toString()) }
                .map {
                    when (it) {
                        is ChosedLanguage.None -> emptyList()
                        is ChosedLanguage.Value -> apiService.getStationsByLanguage(it.value.name)
                    }
                }
                .flowOn(Dispatchers.IO)
                .onEach { plog("${it.size}") }
                .map { stationList ->
                    stationList.sortedByDescending { it.votes }
                        .map {
                            StationModel(
                                name = it.name,
                                upvotes = it.votes.toString(),
                                streamUrl = it.url,
                                country = it.country,
                                homepage = it.homepage,
                                codec = it.codec,
                                bitrate = it.bitrate,
                                favicon = it.favicon,
                                tags = it.tags,
                            )
                        }
                }
                .flowOn(Dispatchers.Default)
                .catch { e -> StationListState.Failure(e) }
                .collect { _state.value = StationListState.Success(it) }
        }
    }
}
