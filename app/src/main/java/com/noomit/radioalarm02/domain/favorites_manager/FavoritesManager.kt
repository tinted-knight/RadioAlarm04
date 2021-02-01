package com.noomit.radioalarm02.domain.favorites_manager

import com.noomit.domain.Favorite
import com.noomit.domain.FavoriteQueries
import com.noomit.domain.StationModel
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesManager @Inject constructor(
    private val queries: FavoriteQueries,
) : FavoritesManagerContract {

    override val allEntries = queries.selectAll().asFlow().mapToList()
        .map {
            it.map { fav ->
                val tagList = fav.tags.split(",").onEach { tag -> tag.trim() }
                StationModel(
                    name = fav.name,
                    upvotes = "",
                    streamUrl = fav.stream_url,
                    country = fav.country,
                    homepage = fav.homepage,
                    codec = "",
                    bitrate = "",
                    favicon = fav.favicon,
                    tags = tagList,
                )
            }
        }

    override fun add(station: StationModel) {
        queries.insertOrUpdate(
            Favorite(
                name = station.name,
                stream_url = station.streamUrl,
                country = station.country,
                homepage = station.homepage,
                favicon = station.favicon,
                tags = station.tags.reduce { acc, s -> "$acc,$s" },
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
