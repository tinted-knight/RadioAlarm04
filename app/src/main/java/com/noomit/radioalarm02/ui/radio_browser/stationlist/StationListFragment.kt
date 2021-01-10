package com.noomit.radioalarm02.ui.radio_browser.stationlist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import com.noomit.playerservice.MediaItem
import com.noomit.radioalarm02.Application00
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.FavoritesViewModelFactory
import com.noomit.radioalarm02.base.PlayerServiceFragment
import com.noomit.radioalarm02.base.collect
import com.noomit.radioalarm02.domain.station_manager.StationManagerState
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.common.textFlow
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserViewModel
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationListAdapter
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filterNotNull

@FlowPreview
class StationListFragment : PlayerServiceFragment<IStationListLayout>() {

    private val viewModel: RadioBrowserViewModel by navGraphViewModels(R.id.nav_radio_browser)

    private val stationViewModel: StationViewModel by viewModels {
        FavoritesViewModelFactory(requireActivity().application as Application00)
    }

    override val layout: View
        get() = StationListLayout(requireContext())

    override val contour: IStationListLayout
        get() = view as IStationListLayout

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

    @FlowPreview
    override fun observeViewModel() {
        collect(viewModel.stationList) {
            when (it) {
                is StationManagerState.Loading -> {
                }
                is StationManagerState.Success -> contour.showContent(it.values)
                is StationManagerState.Failure -> context?.toast(it.error.localizedMessage)
            }
        }
        collect(stationViewModel.nowPlaying.filterNotNull()) {
            service?.mediaItem = MediaItem(url = it.station.streamUrl, title = it.station.name)
            service?.play()
            contour.nowPlaying(it.station, it.inFavorites)
        }
        collect(stationViewModel.uiMessage) {
            context?.toast(
                when (it) {
                    is UIMessage.Added -> getString(R.string.toast_added, it.value)
                    is UIMessage.Removed -> getString(R.string.toast_removed, it.value)
                }
            )
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_searchview, menu)
        val searchItem = menu.findItem(R.id.action_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnCloseListener { false }
            viewModel.applyStationFilter(searchView.textFlow(lifecycleScope))
        }
        super.onCreateOptionsMenu(menu, inflater)
    }
}

