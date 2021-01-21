package com.noomit.radioalarm02.ui.alarm_list.select_melody

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.noomit.playerservice.MediaItem
import com.noomit.radioalarm02.Application00
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.DatabaseViewModelFactory
import com.noomit.radioalarm02.base.PlayerServiceFragment
import com.noomit.radioalarm02.base.collect
import com.noomit.radioalarm02.ui.alarm_list.AlarmManagerViewModel
import com.noomit.radioalarm02.ui.favorites.FavoritesViewModel
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationListAdapter

class SelectMelodyFragment : PlayerServiceFragment<ISelectMelodyLayout>() {

    private val favoritesViewModel: FavoritesViewModel by viewModels {
        DatabaseViewModelFactory(requireActivity().application as Application00)
    }

    private val alarmViewModel: AlarmManagerViewModel by activityViewModels()

    override val layout: View
        get() = SelectMelodyLayout(requireContext())

    override val contour: ISelectMelodyLayout
        get() = view as ISelectMelodyLayout

    override val notificationCaption: String
        get() = getString(R.string.app_name)

    override fun initPlayerViews() {
        playerControlView = contour.playerControll
        playerView = contour.playerView
    }

    override fun prepareView(savedState: Bundle?) {
        val adapter = StationListAdapter(delegate = favoritesViewModel)
        contour.apply {
            setStationsAdapter(adapter)
            showLoading()
        }
        contour.listener = favoritesViewModel
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
