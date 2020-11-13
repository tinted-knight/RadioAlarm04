package com.noomit.radioalarm02.radiobrowserview.viewmodels.stations

import com.noomit.radioalarm02.model.LanguageModel

sealed class StationManagerState {
    object Loading : StationManagerState()
    data class Success(val values: StationList, val language: LanguageModel) : StationManagerState()
    data class Failure(val error: Throwable) : StationManagerState()
}
