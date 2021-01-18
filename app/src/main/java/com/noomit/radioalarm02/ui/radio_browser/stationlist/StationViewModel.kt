package com.noomit.radioalarm02.ui.radio_browser.stationlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noomit.radioalarm02.data.StationModel
import com.noomit.radioalarm02.domain.favorite_manager.IFavoritesManager
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.ItemClickListener
import com.noomit.radioalarm02.ui.radio_browser.stationlist.views.NowPlayingListener
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UIMessage {
    data class Added(val value: String) : UIMessage()
    data class Removed(val value: String) : UIMessage()
    data class OpenExternalLink(val url: String) : UIMessage()
}

class StationViewModel(private val favoritesManager: IFavoritesManager) : ViewModel(),
    ItemClickListener<StationModel>, NowPlayingListener {

    private val _nowPlaying = MutableStateFlow<NowPlaying?>(null)
    val nowPlaying: StateFlow<NowPlaying?> = _nowPlaying

    private val _message = MutableSharedFlow<UIMessage>()
    val uiMessage: SharedFlow<UIMessage> = _message

    override fun onClick(item: StationModel) {
        _nowPlaying.value = NowPlaying(
            station = item,
            inFavorites = favoritesManager.check(item),
        )
    }

    override fun onLongClick(item: StationModel) {
        favoritesManager.add(item)
        viewModelScope.launch { _message.emit(UIMessage.Added(item.name)) }
    }

    override fun onFavoriteClick() {
        _nowPlaying.value?.let {
            if (!it.inFavorites) {
                favoritesManager.add(it.station)
                viewModelScope.launch { _message.emit(UIMessage.Added(it.station.name)) }
                _nowPlaying.value = it.copy(inFavorites = true)
            } else {
                favoritesManager.delete(it.station)
                viewModelScope.launch { _message.emit(UIMessage.Removed(it.station.name)) }
                _nowPlaying.value = it.copy(inFavorites = false)
            }
        }
    }

    override fun onFavoriteLongClick() {}

    override fun onHomePageClick() {
        _nowPlaying.value?.let {
            viewModelScope.launch { _message.emit(UIMessage.OpenExternalLink(it.station.homepage)) }
        }
    }

    override fun onHomePageLongClick() {}
}

data class NowPlaying(
    val station: StationModel,
    val inFavorites: Boolean,
)
