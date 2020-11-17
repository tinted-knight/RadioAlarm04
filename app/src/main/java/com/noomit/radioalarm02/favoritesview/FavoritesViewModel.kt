package com.noomit.radioalarm02.favoritesview

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.Favorite
import com.noomit.radioalarm02.model.StationModel
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app-favorites").i("$message [${Thread.currentThread().name}]")

// #think rewrite all to states
class FavoritesViewModel(database: Database) : ViewModel() {

    private val queries = database.favoriteQueries

    private val _selected = MutableLiveData<Favorite>()
    val selected: LiveData<Favorite> = _selected

    private val _nowPlaying = MutableStateFlow<StationModel?>(null)
    val nowPlaying: StateFlow<StationModel?> = _nowPlaying

    init {
        plog("FavoritesViewModel::init")
    }

    val selectAll = queries.selectAll().asFlow().mapToList().asLiveData()

    fun onClick(item: Favorite) = _selected.postValue(item)

    fun onClick(station: StationModel) {
        _nowPlaying.value = station
    }

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
