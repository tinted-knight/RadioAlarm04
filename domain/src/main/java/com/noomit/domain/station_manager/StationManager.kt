package com.noomit.domain.station_manager

import com.noomit.domain.entities.CategoryModel
import com.noomit.domain.entities.StationModel
import kotlinx.coroutines.flow.Flow

interface StationManager {
  val state: Flow<StationManagerState>

  suspend fun stationsBy(category: CategoryModel)
}

sealed class StationManagerState {
  object Loading : StationManagerState()
  data class Success(val values: List<StationModel>, val category: CategoryModel) : StationManagerState()
  data class Failure(val error: Throwable) : StationManagerState()
}
