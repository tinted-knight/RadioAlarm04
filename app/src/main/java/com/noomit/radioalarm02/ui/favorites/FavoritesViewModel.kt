package com.noomit.radioalarm02.ui.favorites

import com.noomit.domain.entities.StationModel
import com.noomit.domain.favorites_manager.FavoritesManagerContract
import com.noomit.radioalarm02.ui.navigation.NavCommand
import com.noomit.radioalarm02.ui.navigation.NavigationViewModel
import com.noomit.radioalarm02.ui.radio_browser.stationlist.NowPlaying
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.ItemClickListener
import com.noomit.radioalarm02.ui.radio_browser.stationlist.views.NowPlayingListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class FavoritesDirections : NavCommand {
    data class OpenExternalLink(val url: String) : FavoritesDirections()
}

@HiltViewModel
// #think rewrite all to states
class FavoritesViewModel @Inject constructor(
    private val favoritesManager: FavoritesManagerContract,
) :
    NavigationViewModel<FavoritesDirections>(),
    ItemClickListener<StationModel>, NowPlayingListener {

    val selectAll = favoritesManager.allEntries

    private val _nowPlayingForService = MutableStateFlow<NowPlaying?>(null)
    val nowPlayingForService: StateFlow<NowPlaying?> = _nowPlayingForService

    private val _nowPlayingView = MutableStateFlow<NowPlaying?>(null)
    val nowPlayingView: StateFlow<NowPlaying?> = _nowPlayingView

    fun serviceIsPlaying(value: StationModel?) {
        if (value == null) return

        _nowPlayingView.value = NowPlaying(
            station = value,
            inFavorites = favoritesManager.check(value)
        )
    }

    override fun onClick(item: StationModel) {
        val nowPlaying = NowPlaying(
            station = item,
            inFavorites = true,
        )
        _nowPlayingForService.value = nowPlaying
        _nowPlayingView.value = nowPlaying
    }

    override fun onLongClick(item: StationModel) {}

    override fun onFavoriteClick() {}

    override fun onFavoriteLongClick() {
        _nowPlayingForService.value?.let {
            favoritesManager.delete(it.station)
            _nowPlayingForService.value = null
        }
    }

    override fun onHomePageClick() {
        _nowPlayingForService.value?.let {
            navigateTo(FavoritesDirections.OpenExternalLink(it.station.homepage))
        }
    }

    override fun onHomePageLongClick() {}
}
