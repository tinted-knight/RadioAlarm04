package com.noomit.radioalarm02.ui.radio_browser.stationlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull

//class StationListFragment() : PlayerBaseFragment(
//    playerViewId = R.id.exo_player_view,
//    playerControlId = R.id.exo_player_controls,
//    contentLayoutId = R.layout.fragment_station_list,
//) {
//
//    override val viewBinding: FragmentStationListBinding by viewBinding()
//
//    private val viewModel: RadioBrowserViewModel by navGraphViewModels(R.id.nav_radio_browser)
//
//    private val stationViewModel: StationViewModel by viewModels {
//        FavoritesViewModelFactory(requireActivity().application as Application00)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        viewBinding.exoPlayerControls.player = playerView.player
//    }
//
//    override fun prepareUi() {
//        showLoading()
//        viewBinding.rvStationList.apply {
//            setHasFixedSize(true)
//            layoutManager = LinearLayoutManager(requireContext())
//            isVerticalScrollBarEnabled = true
//            adapter = StationListAdapter(
//                onClick = { value ->
//                    stationViewModel.onClick(value)
//                },
//                onLongClick = { value ->
//                    requireContext().toast("long click: ${value.name}")
//                    stationViewModel.onLongClick(value)
//                }
//            )
//            // #todo StationList restore state
////            layoutManager?.onRestoreInstanceState()
//        }
//    }
//
//    override fun listenUiEvents() {
//    }
//
//    override fun observeModel() {
//        lifecycleScope.launchWhenStarted {
//            viewModel.stationList.collect {
//                when (it) {
//                    is StationManagerState.Loading -> showLoading()
//                    is StationManagerState.Success -> showContent(it.values)
//                    is StationManagerState.Failure -> requireContext().toast(it.error.localizedMessage)
//                }
//            }
//        }
//        lifecycleScope.launchWhenStarted {
//            stationViewModel.nowPlaying.filterNotNull().collect(::play)
//        }
//    }
//
//    override fun onServiceConnected() {}
//
//    private fun showContent(values: StationList) = with(viewBinding) {
//        (rvStationList.adapter as StationListAdapter).submitList(values)
//        rvStationList.visibility = View.VISIBLE
//        progressIndicator.visibility = View.GONE
//    }
//
//    private fun showLoading() = with(viewBinding) {
//        progressIndicator.visibility = View.VISIBLE
//        rvStationList.visibility = View.INVISIBLE
//    }
//
//    private fun play(station: StationModel) {
//        service?.mediaItem = MediaItem(station.streamUrl, station.name)
//        service?.play()
//        showNowPlaying(station)
//    }
//
//    private fun showNowPlaying(station: StationModel) {
//        with(viewBinding) {
//            tvStationName.text = station.name
//            Glide.with(requireActivity()).load(station.favicon)
//                .placeholder(R.drawable.ic_radio_24)
//                .into(ivStationIcon)
//            nowPlaying.apply {
//                alpha = 0f
//                translationY = 50f
//                isVisible = true
//                animate().setDuration(300L)
//                    .alpha(1.0f)
//                    .translationY(0f)
//                    .setListener(null)
//            }
//        }
//    }
//}

class StationListFragment : PlayerServiceFragment() {

    private val viewModel: RadioBrowserViewModel by navGraphViewModels(R.id.nav_radio_browser)

    private val stationViewModel: StationViewModel by viewModels {
        FavoritesViewModelFactory(requireActivity().application as Application00)
    }

    private val adapter by lazy(LazyThreadSafetyMode.NONE) { StationListAdapter({}, {}) }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val view = (view as IStationListLayout)
        view.delegate = stationViewModel
        view.setStationsAdapter(adapter)

        view.showLoading()

        lifecycleScope.launchWhenStarted {
            viewModel.stationList.collect {
                when (it) {
                    is StationManagerState.Loading -> {
                    }
                    is StationManagerState.Success -> view.showContent(it.values)
                    is StationManagerState.Failure -> requireContext().toast(it.error.localizedMessage)
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            stationViewModel.nowPlaying.filterNotNull().collect {
                service?.mediaItem = MediaItem(url = it.streamUrl, title = it.name)
                service?.play()
                view.nowPlaying(it)
            }
        }
    }
}
