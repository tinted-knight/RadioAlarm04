package com.noomit.radioalarm02.favoritesview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.Favorite
import com.noomit.radioalarm02.model.StationModel
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app-favorites").i("$message [${Thread.currentThread().name}]")

class FavoritesViewModel(private val database: Database) : ViewModel() {

    private val queries = database.favoriteQueries

    init {
        plog("FavoritesViewModel::init")
    }

    val selectAll = queries.selectAll().asFlow().mapToList().asLiveData()

    fun add(station: StationModel) {
        plog("add favorite")
        queries.insert(
            Favorite(
                name = station.name,
                stream_url = station.streamUrl,
                country = station.country,
                homepage = station.homepage,
                favicon = station.favicon,
                tags = station.tags,
            )
        )
    }
}