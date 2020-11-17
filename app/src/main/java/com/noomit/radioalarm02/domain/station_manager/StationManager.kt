package com.noomit.radioalarm02.domain.station_manager

import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.base.WithLogTag
import com.noomit.radioalarm02.model.LanguageModel
import com.noomit.radioalarm02.model.StationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

typealias StationList = List<StationModel>

class StationManager(via: RadioBrowserService) : WithLogTag {

    override val logTag = "station_manager"

    private val apiService = via

    private val _state = MutableStateFlow<StationManagerState>(StationManagerState.Loading)
    val state: StateFlow<StationManagerState> = _state

    suspend fun stationsBy(language: LanguageModel) {
        val last = state.value
        if (last is StationManagerState.Success && last.language == language) {
            return
        }
        _state.value = StationManagerState.Loading
        apiService.stationsByLanguage(language.name)
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
            .collect { _state.value = StationManagerState.Success(it, language) }
    }
}
