package com.noomit.radioalarm02.radiobrowserview.ui

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.databinding.FragmentStationListBinding
import com.noomit.radioalarm02.radiobrowserview.StationList
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.RadioVMFragment

class StationListFragment : RadioVMFragment(R.layout.fragment_station_list) {

    private val viewBinding: FragmentStationListBinding by viewBinding()

    override fun prepareUi() {
        showLoading()
        viewBinding.rvStationList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            isVerticalScrollBarEnabled = true
            adapter = StationListAdapter { value ->
                requireContext().toast(value.name)
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
                    showContent(stationList)
                },
                onFailure = { err -> requireContext().toast("${err.message}") },
            )
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
}