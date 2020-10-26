package com.noomit.radioalarm02.radiobrowserview.viewmodels.stations

import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.base.WithLogTag
import com.noomit.radioalarm02.model.StationModel
import com.noomit.radioalarm02.radiobrowserview.viewmodels.Action
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

typealias StationList = List<StationModel>

@ExperimentalCoroutinesApi
class StationManager(
    via: RadioBrowserService,
    watchFor: Flow<Action>,
    scope: CoroutineScope,
) : WithLogTag {

    override val logTag = "station_manager"

    private val apiService = via
    private val actions = watchFor

    private val _state = MutableStateFlow<StationManagerState>(StationManagerState.Loading)
    val state: StateFlow<StationManagerState> = _state

    init {
        scope.launch {
            actions.filterIsInstance<Action.Show.StationsByLanguage>()
                .onEach { _state.value = StationManagerState.Loading }
                .flatMapLatest { apiService.stationsByLanguage(it.value.name) }
                .onEach { plog(it.toString()) }
                // #fake delay
                .onEach { delay(500) }
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
                .catch { e -> _state.value = StationManagerState.Failure(e) }
                .collect { _state.value = StationManagerState.Success(it) }
        }
    }
}
