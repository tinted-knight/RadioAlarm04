package com.noomit.domain.favorites_manager

import com.noomit.domain.entities.StationModel
import kotlinx.coroutines.flow.Flow

interface FavoritesManager {
    val allEntries: Flow<List<StationModel>>

    fun add(station: StationModel)
    fun check(station: StationModel): Boolean
    fun delete(station: StationModel)
}
