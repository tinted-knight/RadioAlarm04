package com.noomit.radioalarm02.radiobrowserview.viewmodels.stations

sealed class StationManagerState {
    object Loading : StationManagerState()
    data class Success(val values: StationList) : StationManagerState()
    data class Failure(val error: Throwable) : StationManagerState()
}
