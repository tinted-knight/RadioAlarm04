package com.noomit.radioalarm02.ui.radio_browser.stationlist

import androidx.lifecycle.ViewModel
import com.noomit.radioalarm02.data.StationModel
import com.noomit.radioalarm02.domain.favorite_manager.IFavoritesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StationViewModel(private val favoritesManager: IFavoritesManager) : ViewModel(),
    StationListDelegate {

    private val _nowPlaying = MutableStateFlow<StationModel?>(null)
    val nowPlaying: StateFlow<StationModel?> = _nowPlaying

    override fun onClick(station: StationModel) {
        _nowPlaying.value = station
    }

    fun onLongClick(station: StationModel) {
        favoritesManager.add(station)
    }
}
