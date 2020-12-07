package com.noomit.radioalarm02.ui.radio_browser.stationlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import com.noomit.playerservice.MediaItem
import com.noomit.playerservice.PlayerServiceFragment
import com.noomit.radioalarm02.Application00
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.FavoritesViewModelFactory
import com.noomit.radioalarm02.domain.station_manager.StationManagerState
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserViewModel
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationListAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import timber.log.Timber

private fun plog(message: String) = Timber.tag("tagg-contour").i(message)

class StationListFragment : PlayerServiceFragment() {

    private val viewModel: RadioBrowserViewModel by navGraphViewModels(R.id.nav_radio_browser)

    private val stationViewModel: StationViewModel by viewModels {
        FavoritesViewModelFactory(requireActivity().application as Application00)
    }

    private val contour by lazy(LazyThreadSafetyMode.NONE) { view as IStationListLayout }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return StationListLayout(requireContext())
    }

    override fun onServiceConnected() {}

    override fun initPlayerViews() {
        val view = (view as IStationListLayout)
        playerControlView = view.playerControll
        playerView = view.playerView
    }

    override fun prepareView() {
        val adapter = StationListAdapter(delegate = stationViewModel)
        contour.apply {
            setStationsAdapter(adapter)
            showLoading()
        }
        contour.delegate = stationViewModel
    }

    override fun observeViewModel() {
        collect(viewModel.stationList) {
            plog("collect stationList, $it")
            when (it) {
                is StationManagerState.Loading -> {
                }
                is StationManagerState.Success -> contour.showContent(it.values)
                is StationManagerState.Failure -> requireContext().toast(it.error.localizedMessage)
            }
        }

        collect(stationViewModel.nowPlaying.filterNotNull()) {
            service?.mediaItem = MediaItem(url = it.station.streamUrl, title = it.station.name)
            service?.play()
            contour.nowPlaying(it.station, it.inFavorites)
        }

        collect(stationViewModel.popupMessage) {
            Toast.makeText(requireActivity(), it, Toast.LENGTH_SHORT).show()
        }
    }

    private infix fun <T> Flow<T>.observe(block: suspend (T) -> Unit) =
        lifecycleScope.launchWhenStarted {
            this@observe.collect { block(it) }
        }
}

fun <T> Fragment.collect(values: Flow<T>, block: suspend (T) -> Unit) =
    lifecycleScope.launchWhenStarted {
        values.collect { block(it) }
    }
