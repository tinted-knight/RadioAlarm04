package com.noomit.radioalarm02.domain.station_manager

import com.noomit.radioalarm02.data.CategoryModel

sealed class StationManagerState {
    object Loading : StationManagerState()
    data class Success(val values: StationList, val category: CategoryModel) : StationManagerState()
    data class Failure(val error: Throwable) : StationManagerState()
}
