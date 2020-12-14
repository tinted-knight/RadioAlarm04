package com.noomit.radioalarm02.ui.favorites

import androidx.lifecycle.ViewModel
import com.noomit.radioalarm02.data.StationModel
import com.noomit.radioalarm02.domain.favorite_manager.IFavoritesManager
import com.noomit.radioalarm02.ui.radio_browser.stationlist.NowPlaying
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.ItemClickListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app-favorites").i("$message [${Thread.currentThread().name}]")

// #think rewrite all to states
class FavoritesViewModel(private val favoritesManager: IFavoritesManager) : ViewModel(),
    ItemClickListener<StationModel>, FavoritesViewListener {

    val selectAll = favoritesManager.allEntries

    private val _nowPlaying = MutableStateFlow<NowPlaying?>(null)
    val nowPlaying: StateFlow<NowPlaying?> = _nowPlaying

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
}
