package com.noomit.radioalarm02.ui.radio_browser.stationlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noomit.domain.entities.StationModel
import com.noomit.domain.favorites_manager.FavoritesManager
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.ItemClickListener
import com.noomit.radioalarm02.ui.radio_browser.stationlist.views.NowPlayingListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class UIMessage {
  data class Added(val value: String) : UIMessage()
  data class Removed(val value: String) : UIMessage()
  data class OpenExternalLink(val url: String) : UIMessage()
}

@HiltViewModel
class StationViewModel @Inject constructor(
  private val favoritesManager: FavoritesManager,
) : ViewModel(),
  ItemClickListener<StationModel>, NowPlayingListener {

  private val _nowPlayingForService = MutableStateFlow<NowPlaying?>(null)
  val nowPlayingForService: StateFlow<NowPlaying?> = _nowPlayingForService

  private val _nowPlayingView = MutableStateFlow<NowPlaying?>(null)
  val nowPlayingView: StateFlow<NowPlaying?> = _nowPlayingView

  private val _message = MutableSharedFlow<UIMessage>()
  val uiMessage: SharedFlow<UIMessage> = _message

  override fun onClick(item: StationModel) {
    val nowPlaying = NowPlaying(
      station = item,
      inFavorites = favoritesManager.check(item),
    )
    _nowPlayingForService.value = nowPlaying
    _nowPlayingView.value = nowPlaying
  }

  override fun onLongClick(item: StationModel) {
    favoritesManager.add(item)
    viewModelScope.launch { _message.emit(UIMessage.Added(item.name)) }
  }

  override fun onFavoriteClick() {
    _nowPlayingForService.value?.let {
      if (!it.inFavorites) {
        favoritesManager.add(it.station)
        viewModelScope.launch { _message.emit(UIMessage.Added(it.station.name)) }
        _nowPlayingView.value = it.copy(inFavorites = true)
      } else {
        favoritesManager.delete(it.station)
        viewModelScope.launch { _message.emit(UIMessage.Removed(it.station.name)) }
        _nowPlayingView.value = it.copy(inFavorites = false)
      }
    }
  }

  override fun onFavoriteLongClick() {}

  override fun onHomePageClick() {
    _nowPlayingForService.value?.let {
      viewModelScope.launch { _message.emit(UIMessage.OpenExternalLink(it.station.homepage)) }
    }
  }

  override fun onHomePageLongClick() {}

//    override fun onVolumeUp() {
//        TODO("Not yet implemented")
//    }
//
//    override fun onVolumeDown() {
//        TODO("Not yet implemented")
//    }
}

data class NowPlaying(
  val station: StationModel,
  val inFavorites: Boolean,
  val playImmediately: Boolean = true,
)
