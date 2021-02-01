package com.noomit.domain.station_manager

import com.noomit.domain.CategoryModel
import com.noomit.domain.StationModel
import kotlinx.coroutines.flow.Flow

interface StationManagerContract {
    val state: Flow<StationManagerState>
    suspend fun stationsBy(category: CategoryModel)
}

typealias StationList = List<StationModel>

sealed class StationManagerState {
    object Loading : StationManagerState()
    data class Success(val values: StationList, val category: CategoryModel) : StationManagerState()
    data class Failure(val error: Throwable) : StationManagerState()
}