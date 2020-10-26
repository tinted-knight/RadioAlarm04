package com.noomit.radioalarm02.radiobrowserview.viewmodels

import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.model.CategoryModel
import com.noomit.radioalarm02.model.StationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class StationManager(
    private val apiService: RadioBrowserService,
    chosenFlow: Flow<CategoryModel?>,
) {
    val stationListFlow: Flow<StationListResponse> = chosenFlow
        .map { if (it == null) emptyList() else apiService.getStationsByLanguage(it.name) }
        .flowOn(Dispatchers.IO)
        .map { list ->
            Result.success(list.sortedByDescending { it.votes }
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
                        tags = it.tags)
                })
        }
        .flowOn(Dispatchers.Default)
        .catch { emit(Result.failure(Exception(it.localizedMessage))) }
}
