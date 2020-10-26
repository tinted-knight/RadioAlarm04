package com.noomit.radioalarm02.radiobrowserview.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.bumptech.glide.load.HttpException
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.model.LanguageModel
import com.noomit.radioalarm02.model.StationModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

typealias StationList = List<StationModel>

typealias StationListResponse = Result<StationList>

class StationManager(via: RadioBrowserService, observe: LiveData<LanguageModel>) {
    private val chosenLanguage = observe
    private val apiService = via

    val values = chosenLanguage.switchMap {
        liveData {
            try {
                emit(Result.success(emptyList()))
                val stationList = withContext(Dispatchers.IO) {
                    apiService.getStationsByLanguage(it.name)
                }
                // #think
                if (stationList.isNullOrEmpty()) {
                    emit(Result.success(emptyList<StationModel>()))
                } else {
                    // #fake delay
                    delay(500)
                    val forViewList = stationList
                        .sortedByDescending { it.votes }
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
                    emit(Result.success(forViewList))
                }
            } catch (e: HttpException) {
                emit(Result.failure<StationList>(Exception("http exception")))
            }
        }
    }
}
