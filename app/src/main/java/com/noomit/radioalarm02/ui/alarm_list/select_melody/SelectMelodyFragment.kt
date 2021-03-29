package com.noomit.radioalarm02.ui.alarm_list.select_melody

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.service.ServiceMediaItem
import com.noomit.radioalarm02.ui.alarm_list.HomeViewModel
import com.noomit.radioalarm02.ui.favorites.FavoritesViewModel
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationListAdapter
import com.noomit.radioalarm02.util.fragment.PlayerServiceFragment
import com.noomit.radioalarm02.util.fragment.collect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectMelodyFragment : PlayerServiceFragment<ISelectMelodyLayout>() {

    private val favoritesViewModel: FavoritesViewModel by viewModels()

    private val viewModel: HomeViewModel by activityViewModels()

    override val layout: View
        get() = SelectMelodyLayout(requireContext())

    override val contour: ISelectMelodyLayout
        get() = view as ISelectMelodyLayout

    override val notificationCaption: String
        get() = getString(R.string.app_name)

    override fun initPlayerViews() {
        playerControlView = contour.playerControl
    }

    override fun prepareView(savedState: Bundle?) {
        val adapter = StationListAdapter(delegate = favoritesViewModel)
        contour.apply {
            setStationsAdapter(adapter)
            showLoading()
        }
        contour.listener = favoritesViewModel
        contour.onSetMelodyClick = {
            favoritesViewModel.nowPlayingForService.value?.let {
                viewModel.setMelody(it.station)
            }
            findNavController().popBackStack()
        }
        contour.onSetDefaultRingtone = {
            viewModel.setDefaultRingtone()
            findNavController().popBackStack()
        }
    }

    override fun observeViewModel() {
        collect(favoritesViewModel.selectAll) {
            contour.showContent(it)
        }

        collect(favoritesViewModel.nowPlayingForService) {
            if (it != null) {
                service?.mediaItem = ServiceMediaItem(url = it.station.streamUrl, title = it.station.name)
                service?.play()
                contour.nowPlaying(it.station, it.inFavorites)
            } else {
                service?.pause()
                contour.nowPlayingEmpty()
            }
        }
    }

    override fun onStop() {
        service?.pause()
        super.onStop()
    }
}
