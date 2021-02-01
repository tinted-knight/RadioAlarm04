package com.noomit.radioalarm02.domain.favorites_manager

import com.noomit.radioalarm02.data.StationModel
import kotlinx.coroutines.flow.Flow

interface FavoritesManagerContract {
    val allEntries: Flow<List<StationModel>>
    fun add(station: StationModel)
    fun check(station: StationModel): Boolean
    fun delete(station: StationModel)
}
