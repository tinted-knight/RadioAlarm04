package com.noomit.radioalarm02.ui.radio_browser.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.example.radiobrowser.ActiveServerState
import com.noomit.radioalarm02.Application00
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.ContourFragment
import com.noomit.radioalarm02.base.ViewModelFactory
import com.noomit.radioalarm02.base.collect
import com.noomit.radioalarm02.data.CategoryModel
import com.noomit.radioalarm02.domain.server_manager.ServerState
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserViewModel
import com.squareup.contour.utils.children
import kotlinx.coroutines.FlowPreview

@FlowPreview
class RadioBrowserFragment : ContourFragment<IRadioBrowserHomeLayout>() {

    private val viewModel: RadioBrowserViewModel by navGraphViewModels(
        navGraphId = R.id.nav_radio_browser,
        factoryProducer = { ViewModelFactory(requireActivity().application as Application00) }
    )

    private val adapter by lazy(LazyThreadSafetyMode.NONE) { ServerListAdapter(adapterListener) }

    override val contour: IRadioBrowserHomeLayout
        get() = (view as ViewGroup).children.first() as IRadioBrowserHomeLayout

    override val layout: View
        get() {
            val scrollView = ScrollView(context)
            scrollView.addView(RadioBrowserHomeLayout(requireContext()))
            scrollView.isVerticalScrollBarEnabled = true

            return scrollView
        }

    override fun prepareView() {
        contour.apply {
            delegate = listener
            setServerAdapter(adapter)
            showLoading()
        }
    }

    override fun observeViewModel() {
        collect(viewModel.availableServers) {
            when (it) {
                is ServerState.Loading -> contour.showLoading()
                is ServerState.Values -> contour.update(content = it.values)
                is ServerState.Failure -> requireContext().toast(it.e.localizedMessage)
            }
        }

        collect(viewModel.activeServer) {
            when (it) {
                is ActiveServerState.None -> contour.update(activerServer = null)
                is ActiveServerState.Value -> contour.update(activerServer = it.serverInfo)
            }
        }
    }

    private val listener = object : RadioBrowserHomeDelegate {
        override fun onLanguageClick() {
            viewModel.getLanguageList()
            findNavController().navigate(
                R.id.action_radioBrowser_to_languageList,
                Bundle().apply { putString("title", "Languages") }
            )
        }

        override fun onTagClick() {
            viewModel.getTagList()
            findNavController().navigate(
                R.id.action_radioBrowser_to_languageList,
                Bundle().apply { putString("title", "Tags") }
            )
        }

        override fun onTopVotedClick() {
            viewModel.showStations(CategoryModel.TopVoted())
            findNavController().navigate(
                R.id.action_radioBrowser_to_stationList,
                Bundle().apply { putString("title", "Top voted") }
            )
        }
    }

    private val adapterListener: ServerClick = {
        viewModel.setServer(it)
        contour.serverListCollapse()
    }
}
