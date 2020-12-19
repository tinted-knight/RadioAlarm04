package com.noomit.radioalarm02.ui.alarm_list.select_melody

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.noomit.playerservice.MediaItem
import com.noomit.playerservice.PlayerServiceFragment
import com.noomit.radioalarm02.Application00
import com.noomit.radioalarm02.base.DatabaseViewModelFactory
import com.noomit.radioalarm02.base.collect
import com.noomit.radioalarm02.ui.alarm_list.AlarmManagerViewModel
import com.noomit.radioalarm02.ui.favorites.FavoritesViewModel
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationListAdapter

class SelectMelodyFragment : PlayerServiceFragment() {

    private val favoritesViewModel: FavoritesViewModel by viewModels {
        DatabaseViewModelFactory(requireActivity().application as Application00)
    }

    private val alarmViewModel: AlarmManagerViewModel by activityViewModels()

    private val contour: ISelectMelodyLayout
        get() = view as ISelectMelodyLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return SelectMelodyLayout(requireContext())
    }

    override fun onServiceConnected() {}

    override fun initPlayerViews() {
        playerControlView = contour.playerControll
        playerView = contour.playerView
    }

    override fun prepareView() {
        val adapter = StationListAdapter(delegate = favoritesViewModel)
        contour.apply {
            setStationsAdapter(adapter)
            showLoading()
        }
        contour.delegate = favoritesViewModel
        contour.onSetMelodyClick = {
            favoritesViewModel.nowPlaying.value?.let {
                alarmViewModel.setMelody(it.station)
            }
            findNavController().popBackStack()
        }
        contour.onSetDefaultRingtone = {
            alarmViewModel.setDefaultRingtone()
            findNavController().popBackStack()
        }
    }

    override fun observeViewModel() {
        collect(favoritesViewModel.selectAll) {
            contour.showContent(it)
        }

        collect(favoritesViewModel.nowPlaying) {
            if (it != null) {
                service?.mediaItem = MediaItem(url = it.station.streamUrl, title = it.station.name)
                service?.play()
                contour.nowPlaying(it.station, it.inFavorites)
            } else {
                service?.stop()
                contour.nowPlayingEmpty()
            }
        }
    }
}
