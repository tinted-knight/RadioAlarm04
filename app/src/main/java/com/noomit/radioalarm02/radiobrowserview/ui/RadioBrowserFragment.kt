package com.noomit.radioalarm02.radiobrowserview.ui

import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.ServerInfo
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.BaseFragment
import com.noomit.radioalarm02.base.ViewModelFactory
import com.noomit.radioalarm02.databinding.FragmentRadioBrowserBinding
import com.noomit.radioalarm02.radiobrowserview.adapters.ServerListAdapter
import com.noomit.radioalarm02.radiobrowserview.viewmodels.Action
import com.noomit.radioalarm02.radiobrowserview.viewmodels.RadioBrowserViewModel
import com.noomit.radioalarm02.radiobrowserview.viewmodels.ServerState
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.viewHide
import com.noomit.radioalarm02.viewShow
import kotlinx.coroutines.flow.collect

class RadioBrowserFragment() : BaseFragment(R.layout.fragment_radio_browser) {

    override val viewBinding: FragmentRadioBrowserBinding by viewBinding()

    private val viewModel: RadioBrowserViewModel by navGraphViewModels(
        navGraphId = R.id.nav_radio_browser,
        factoryProducer = { ViewModelFactory(RadioBrowserService()) }
    )

    override fun prepareUi() {
        showLoading()
        viewBinding.rvServers.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            isVerticalScrollBarEnabled = true
            adapter = ServerListAdapter(
                onServerClick = { serverInfo ->
                    viewModel.setServer(serverInfo)
                }
            )
            // #todo StationList restore state
//            layoutManager?.onRestoreInstanceState()
        }
    }

    override fun observeModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.availableServers.collect {
                when (it) {
                    is ServerState.Loading -> showLoading()
                    is ServerState.Values -> showContent(it.values)
                    is ServerState.Failure -> requireContext().toast(it.e.localizedMessage)
                }
            }
        }
    }

    override fun listenUiEvents() = with(viewBinding) {
        btnLanguages.setOnClickListener {
            viewModel.offer(Action.Click.LanguageList)
            findNavController().navigate(R.id.action_radioBrowser_to_languageList)
        }
    }

    private fun showContent(values: List<ServerInfo>) = with(viewBinding) {
        (rvServers.adapter as ServerListAdapter).submitList(values)
        rvServers.viewShow()
        progressIndicator.viewHide()
        btnLanguages.isEnabled = true
        btnTags.isEnabled = true
        btnTopVoted.isEnabled = true
        btnAllStations.isEnabled = true

        btnSearch.isEnabled = true
        etSearchName.isEnabled = true
        etSearchTag.isEnabled = true
    }

    private fun showLoading() = with(viewBinding) {
        progressIndicator.viewShow()
        rvServers.viewHide()
        btnLanguages.isEnabled = false
        btnTags.isEnabled = false
        btnTopVoted.isEnabled = false
        btnAllStations.isEnabled = false

        btnSearch.isEnabled = false
        etSearchName.isEnabled = false
        etSearchTag.isEnabled = false
    }
}
