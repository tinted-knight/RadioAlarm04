package com.noomit.domain.radio_browser

import com.noomit.domain.entities.CategoryNetworkEntity
import com.noomit.domain.entities.StationNetworkEntity
import kotlinx.coroutines.flow.Flow

interface RadioBrowser {
    fun setActiveServer(serverInfo: ServerInfo)
    suspend fun getLanguageList(): List<CategoryNetworkEntity>
    suspend fun getTagList(): List<CategoryNetworkEntity>
    suspend fun stationsByLanguage(langString: String): List<StationNetworkEntity>
    suspend fun stationsByTag(tagString: String): List<StationNetworkEntity>
    suspend fun getAllStations(): List<StationNetworkEntity>
    suspend fun getTopVoted(): List<StationNetworkEntity>
    suspend fun search(name: String, tag: String): List<StationNetworkEntity>
}
