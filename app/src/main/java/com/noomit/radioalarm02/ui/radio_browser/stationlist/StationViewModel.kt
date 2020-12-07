package com.noomit.radioalarm02.ui.radio_browser.stationlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noomit.radioalarm02.data.StationModel
import com.noomit.radioalarm02.domain.favorite_manager.IFavoritesManager
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.ItemClickListener
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StationViewModel(private val favoritesManager: IFavoritesManager) : ViewModel(),
    ItemClickListener<StationModel>, StationListViewListener {

    private val _nowPlaying = MutableStateFlow<NowPlaying?>(null)
    val nowPlaying: StateFlow<NowPlaying?> = _nowPlaying

    private val _message = MutableSharedFlow<String>()
    val popupMessage: SharedFlow<String> = _message

    override fun onClick(item: StationModel) {
        _nowPlaying.value = NowPlaying(
            station = item,
            inFavorites = favoritesManager.check(item),
        )
    }

    override fun onLongClick(item: StationModel) {
        favoritesManager.add(item)
        viewModelScope.launch { _message.emit("To favorites: ${item.name}") }
    }

    override fun onFavoriteClick() {
        _nowPlaying.value?.let {
            favoritesManager.add(it.station)
            viewModelScope.launch { _message.emit("To favorites: ${it.station.name}") }
            _nowPlaying.value = it.copy(inFavorites = true)
        }
    }
}

data class NowPlaying(
    val station: StationModel,
    val inFavorites: Boolean,
)
