package com.noomit.radioalarm02.ui.favorites

import androidx.hilt.lifecycle.ViewModelInject
import com.noomit.radioalarm02.data.StationModel
import com.noomit.radioalarm02.domain.favorite_manager.FavoritesManagerContract
import com.noomit.radioalarm02.tplog
import com.noomit.radioalarm02.ui.navigation.NavCommand
import com.noomit.radioalarm02.ui.navigation.NavigationViewModel
import com.noomit.radioalarm02.ui.radio_browser.stationlist.NowPlaying
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.ItemClickListener
import com.noomit.radioalarm02.ui.radio_browser.stationlist.views.NowPlayingListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

sealed class FavoritesDirections : NavCommand {
    data class OpenExternalLink(val url: String) : FavoritesDirections()
}

// #think rewrite all to states
class FavoritesViewModel @ViewModelInject constructor(
    private val favoritesManager: FavoritesManagerContract,
) :
    NavigationViewModel<FavoritesDirections>(),
    ItemClickListener<StationModel>, NowPlayingListener {

    val selectAll = favoritesManager.allEntries

    private val _nowPlaying = MutableStateFlow<NowPlaying?>(null)
    val nowPlaying: StateFlow<NowPlaying?> = _nowPlaying

    init {
        tplog("FavoritesViewModel::init")
    }

    override fun onCleared() {
        tplog("FavoritesViewModel::onCleared")
        super.onCleared()
    }

    override fun onClick(item: StationModel) {
        _nowPlaying.value = NowPlaying(
            station = item,
            inFavorites = true,
        )
    }

    override fun onLongClick(item: StationModel) {}

    override fun onFavoriteClick() {
        _nowPlaying.value?.let {
            favoritesManager.delete(it.station)
            _nowPlaying.value = null
        }
    }

    override fun onFavoriteLongClick() {}

    override fun onHomePageClick() {
        _nowPlaying.value?.let {
            navigateTo(FavoritesDirections.OpenExternalLink(it.station.homepage))
        }
    }

    override fun onHomePageLongClick() {}

    override fun showingExtended() {}
}
