package com.noomit.radioalarm02.domain.favorite_manager

import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.Favorite
import com.noomit.radioalarm02.data.StationModel
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow

interface IFavoritesManager {
    val allEntries: Flow<List<Favorite>>
    fun add(station: StationModel)
    fun check(station: StationModel): Boolean
    fun delete(station: StationModel)
}

class FavoritesManager(database: Database) : IFavoritesManager {
    private val queries = database.favoriteQueries

    override val allEntries = queries.selectAll().asFlow().mapToList()

    override fun add(station: StationModel) {
        queries.insertOrUpdate(
            Favorite(
                name = station.name,
                stream_url = station.streamUrl,
                country = station.country,
                homepage = station.homepage,
                favicon = station.favicon,
                tags = station.tags.reduce { acc, s -> acc + s },
            )
        )
    }

    override fun check(station: StationModel): Boolean {
        val favorite = queries.selectByStreamUrl(station.streamUrl).executeAsOneOrNull()
        return favorite != null
    }

    override fun delete(station: StationModel) {
        queries.delete(station.streamUrl)
    }
}