package com.noomit.radioalarm02.radiobrowserview.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.playerservice.MediaItem
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.databinding.FragmentStationListBinding
import com.noomit.radioalarm02.radiobrowserview.StationList
import com.noomit.radioalarm02.radiobrowserview.model.StationModel
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.PlayerVMFragment

class StationListFragment() :
    PlayerVMFragment(R.id.exo_player_view, R.layout.fragment_station_list) {

    private val viewBinding: FragmentStationListBinding by viewBinding()

    override fun prepareUi() {
        showLoading()
        viewBinding.rvStationList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            isVerticalScrollBarEnabled = true
            adapter = StationListAdapter { value ->
                requireContext().toast(value.name)
                play(value)
            }
            // #todo StationList restore state
//            layoutManager?.onRestoreInstanceState()
        }
    }

    override fun listenUiEvents() {
    }

    override fun observeModel() = with(viewModel) {
        stationList.observe(viewLifecycleOwner) {
            it.fold(
                onSuccess = { stationList ->
                    if (stationList.isEmpty()) showLoading() else showContent(stationList)
                },
                onFailure = { err -> requireContext().toast("${err.message}") },
            )
        }
    }

    override fun renderPlayingView() {
        when (isPlaying) {
            true -> {
            }
            false -> {
            }
        }
    }

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
        isPlaying = true
    }

    private fun stop() {
        service?.stop()
        isPlaying = false
    }
}