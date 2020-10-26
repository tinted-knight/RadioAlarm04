package com.noomit.radioalarm02.radiobrowserview.ui

import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.radiobrowser.ServerInfo
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.BaseFragment
import com.noomit.radioalarm02.databinding.FragmentRadioBrowserBinding
import com.noomit.radioalarm02.radiobrowserview.adapters.ServerListAdapter
import com.noomit.radioalarm02.radiobrowserview.viewmodels.RadioBrowserViewModel
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.viewHide
import com.noomit.radioalarm02.viewShow

class RadioBrowserFragment() : BaseFragment(R.layout.fragment_radio_browser) {

    override val viewBinding: FragmentRadioBrowserBinding by viewBinding()

    private val viewModel: RadioBrowserViewModel by activityViewModels()

    override fun prepareUi() {
        showLoading()
        viewBinding.rvServers.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            isVerticalScrollBarEnabled = true
            adapter = ServerListAdapter(
                onServerClick = {serverInfo ->
                    viewModel.setServer(serverInfo)
                }
            )
            // #todo StationList restore state
//            layoutManager?.onRestoreInstanceState()
        }
    }

    override fun observeModel() {
        viewModel.availableServers.observe(viewLifecycleOwner) {
            it.fold(
                onSuccess = { values ->
                    showContent(values)
                },
                onFailure = { e ->
                    requireActivity().toast(e.localizedMessage)
                },
            )
        }
    }

    override fun listenUiEvents() = with(viewBinding) {
        btnLanguages.setOnClickListener {
            findNavController().navigate(R.id.action_radioBrowser_to_languageList)
        }
    }

    private fun showContent(values: List<ServerInfo>) = with(viewBinding) {
        (rvServers.adapter as ServerListAdapter).submitList(values)
        rvServers.viewShow()
        progressIndicator.viewHide()
    }

    private fun showLoading() = with(viewBinding) {
        progressIndicator.viewShow()
        rvServers.viewHide()
    }
}
