package com.noomit.radioalarm02.ui.radio_browser.stationlist

import android.os.Bundle
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.navGraphViewModels
import com.noomit.playerservice.MediaItem
import com.noomit.playerservice.PlayerServiceFragment
import com.noomit.radioalarm02.Application00
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.FavoritesViewModelFactory
import com.noomit.radioalarm02.base.collect
import com.noomit.radioalarm02.domain.station_manager.StationManagerState
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserViewModel
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationListAdapter
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.filterNotNull
import timber.log.Timber

private fun plog(message: String) = Timber.tag("tagg-contour").i(message)

@FlowPreview
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

    @FlowPreview
    override fun observeViewModel() {
        collect(viewModel.stationList) {
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_searchview, menu)
        val searchItem = menu.findItem(R.id.action_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnCloseListener {
                return@setOnCloseListener true
            }
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.applyFilter(query)
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.applyFilter(newText)
                    return true
                }
            })
        }
        super.onCreateOptionsMenu(menu, inflater)
    }
}

