package com.noomit.radioalarm02.ui.favorites

import com.noomit.domain.entities.StationModel
import com.noomit.domain.favorites_manager.FavoritesManagerContract
import com.noomit.radioalarm02.ui.navigation.NavigationViewModel
import com.noomit.radioalarm02.ui.navigation.OneShotEvent
import com.noomit.radioalarm02.ui.radio_browser.stationlist.NowPlaying
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.ItemClickListener
import com.noomit.radioalarm02.ui.radio_browser.stationlist.views.NowPlayingListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

sealed class FavoritesEvent : OneShotEvent {
    data class OpenExternalLink(val url: String) : FavoritesEvent()
    object VolumeUp : FavoritesEvent()
    object VolumeDown : FavoritesEvent()
}

@HiltViewModel
// #think rewrite all to states
class FavoritesViewModel @Inject constructor(
    private val favoritesManager: FavoritesManagerContract,
) : NavigationViewModel<FavoritesEvent>(),
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

    override fun onFavoriteClick() {
        _nowPlayingView.value?.let {
            if (!it.inFavorites) {
                favoritesManager.add(it.station)
                _nowPlayingView.value = it.copy(inFavorites = true)
                _nowPlayingForService.value = it.copy(inFavorites = true, playImmediately = false)
            }
        }
    }

    override fun onFavoriteLongClick() {
        _nowPlayingForService.value?.let {
            favoritesManager.delete(it.station)
            _nowPlayingView.value = it.copy(inFavorites = false)
            _nowPlayingForService.value = null
        }
    }

    override fun onHomePageClick() {
        _nowPlayingForService.value?.let {
            navigateTo(FavoritesEvent.OpenExternalLink(it.station.homepage))
        }
    }

    override fun onHomePageLongClick() {}

//    override fun onVolumeUp() {
//        navigateTo(FavoritesDirections.VolumeUp)
//    }
//
//    override fun onVolumeDown() {
//        navigateTo(FavoritesDirections.VolumeDown)
//    }
}
