package com.noomit.radioalarm02.ui.radio_browser.stationlist

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.playerservice.MediaItem
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.DatabaseViewModelFactory
import com.noomit.radioalarm02.base.PlayerBaseFragment
import com.noomit.radioalarm02.databinding.FragmentStationListBinding
import com.noomit.radioalarm02.domain.station_manager.StationList
import com.noomit.radioalarm02.domain.station_manager.StationManagerState
import com.noomit.radioalarm02.favoritesview.FavoritesViewModel
import com.noomit.radioalarm02.model.AppDatabase
import com.noomit.radioalarm02.model.StationModel
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserViewModel
import kotlinx.coroutines.flow.collect

class StationListFragment() : PlayerBaseFragment(
    playerViewId = R.id.exo_player_view,
    playerControlId = R.id.exo_player_controls,
    contentLayoutId = R.layout.fragment_station_list,
) {

    override val viewBinding: FragmentStationListBinding by viewBinding()

    private val viewModel: RadioBrowserViewModel by navGraphViewModels(R.id.nav_radio_browser)

    private val favoritesViewModel: FavoritesViewModel by viewModels {
        DatabaseViewModelFactory(AppDatabase.getInstance(requireActivity()))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.exoPlayerControls.player = playerView.player
    }

    override fun prepareUi() {
        showLoading()
        viewBinding.rvStationList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            isVerticalScrollBarEnabled = true
            adapter = StationListAdapter(
                onClick = { value ->
                    requireContext().toast(value.name)
                    play(value)
                },
                onLongClick = { value ->
                    requireContext().toast("long click: ${value.name}")
                    favoritesViewModel.add(value)
                }
            )
            // #todo StationList restore state
//            layoutManager?.onRestoreInstanceState()
        }
    }

    override fun listenUiEvents() {
    }

    override fun observeModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.stationList.collect {
                when (it) {
                    is StationManagerState.Loading -> showLoading()
                    is StationManagerState.Success -> showContent(it.values)
                    is StationManagerState.Failure -> requireContext().toast(it.error.localizedMessage)
                }
            }
        }
    }

    override fun onServiceConnected() {}

    private fun showContent(values: StationList) = with(viewBinding) {
        (rvStationList.adapter as StationListAdapter).submitList(values)
        rvStationList.visibility = View.VISIBLE
        progressIndicator.visibility = View.GONE
    }

    private fun showLoading() = with(viewBinding) {
        progressIndicator.visibility = View.VISIBLE
        rvStationList.visibility = View.INVISIBLE
    }

    private fun play(station: StationModel) {
        service?.mediaItem = MediaItem(station.streamUrl, station.name)
        service?.play()
    }
}
