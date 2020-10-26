package com.noomit.radioalarm02.radiobrowserview.viewmodels.stations

sealed class StationListState {
    object Loading : StationListState()
    data class Success(val values: StationList) : StationListState()
    data class Failure(val error: Throwable) : StationListState()
}
