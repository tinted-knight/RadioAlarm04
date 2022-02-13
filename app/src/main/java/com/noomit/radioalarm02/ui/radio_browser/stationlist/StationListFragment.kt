package com.noomit.radioalarm02.ui.radio_browser.stationlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import com.noomit.domain.station_manager.StationManagerState
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.service.ServiceMediaItem
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.common.textFlow
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserViewModel
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationListAdapter
import com.noomit.radioalarm02.util.fragment.PlayerServiceFragment
import com.noomit.radioalarm02.util.fragment.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filterNotNull

@FlowPreview
@AndroidEntryPoint
class StationListFragment : PlayerServiceFragment<IStationListLayout>() {

    private val viewModel: RadioBrowserViewModel by hiltNavGraphViewModels(R.id.nav_radio_browser)

    private val stationViewModel: StationViewModel by viewModels()

    override val layout: View
        get() = StationListLayout(requireContext())

    override val contour: IStationListLayout
        get() = view as IStationListLayout

    override val notificationCaption: String
        get() = getString(R.string.app_name)

    private var recyclerState: Parcelable? = null

    override fun initPlayerViews() {
        val view = (view as IStationListLayout)
        playerControlView = view.playerControl
    }

    override fun prepareView(savedState: Bundle?) {
        val adapter = StationListAdapter(delegate = stationViewModel)
        contour.apply {
            setStationsAdapter(adapter)
            showLoading()
            recyclerState?.let {
                contour.setRecyclerState(it)
                return@apply
            }
            savedState?.let { bundle ->
                bundle.getParcelable<Parcelable>(RECYCLER_STATE)?.let { state ->
                    contour.setRecyclerState(state)
                }
            }
        }
        contour.listener = stationViewModel
    }

    override fun observeViewModel() {
        collect(viewModel.stationList) {
            when (it) {
                is StationManagerState.Loading -> {
                }
                is StationManagerState.Success -> contour.showContent(it.values)
                is StationManagerState.Failure -> context?.toast(it.error.localizedMessage)
                // #todo smth like showError or empty
                else -> contour.showLoading()
            }
        }
        collect(stationViewModel.nowPlayingView) {
            if (it == null) {
                contour.nowPlayingEmpty()
            } else {
                contour.nowPlaying(it.station, it.inFavorites)
            }
        }
        collect(stationViewModel.nowPlayingForService.filterNotNull()) {
            service?.mediaItem = ServiceMediaItem(url = it.station.streamUrl, title = it.station.name)
            service?.playingModel = it.station
            service?.play()
            contour.nowPlaying(it.station, it.inFavorites)
        }
        collect(stationViewModel.uiMessage) { message ->
            when (message) {
                is UIMessage.Added -> context?.toast(getString(R.string.toast_added, message.value))
                is UIMessage.Removed -> context?.toast(getString(R.string.toast_removed, message.value))
                is UIMessage.OpenExternalLink -> startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(message.url)
                    }
                )
            }
        }
    }

    override fun onPause() {
        val state = contour.getRecyclerState()
        recyclerState = state
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(RECYCLER_STATE, recyclerState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

