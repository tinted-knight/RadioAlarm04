package com.noomit.radioalarm02.ui.radio_browser.stationlist

import androidx.lifecycle.ViewModel
import com.noomit.radioalarm02.data.StationModel
import com.noomit.radioalarm02.domain.favorite_manager.IFavoritesManager
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.ItemClickListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StationViewModel(private val favoritesManager: IFavoritesManager) : ViewModel(),
    ItemClickListener<StationModel> {

    private val _nowPlaying = MutableStateFlow<StationModel?>(null)
    val nowPlaying: StateFlow<StationModel?> = _nowPlaying

    private val _message = MutableStateFlow<String?>(null)
    val popupMessage: StateFlow<String?> = _message

    override fun onClick(item: StationModel) {
        _nowPlaying.value = item
    }

    override fun onLongClick(item: StationModel) {
        favoritesManager.add(item)
    }
}
