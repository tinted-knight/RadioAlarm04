package com.noomit.radioalarm02.ui.radio_browser.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.noomit.domain.server_manager.ServerState
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.ui.navigation.NavHelper
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserEvent
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserViewModel
import com.noomit.radioalarm02.util.fragment.ContourFragment
import com.noomit.radioalarm02.util.fragment.collect
import com.squareup.contour.utils.children
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

@FlowPreview
@AndroidEntryPoint
class RadioBrowserFragment : ContourFragment<IRadioBrowserHomeLayout>() {

    private val viewModel: RadioBrowserViewModel by navGraphViewModels(R.id.nav_radio_browser) {
        defaultViewModelProviderFactory
    }

    override val contour: IRadioBrowserHomeLayout
        get() = (view as ViewGroup).children.first() as IRadioBrowserHomeLayout

    override val layout: View
        get() {
            val scrollView = ScrollView(context)
            scrollView.addView(RadioBrowserHomeLayout(requireContext()))
            scrollView.isVerticalScrollBarEnabled = true

            return scrollView
        }

    override fun prepareView(savedState: Bundle?) {
        contour.apply {
            val adapter = ServerListAdapter(adapterListener)
            delegate = viewModel
            setServerAdapter(adapter)
            showLoading()
            btnSearchEnabled(false)
            setSearchFields(viewModel.searchState.value.name, viewModel.searchState.value.tag)
        }
    }

    override fun observeViewModel() {
        collect(viewModel.availableServers) {
            when (it) {
                is ServerState.Loading -> contour.showLoading()
                is ServerState.Values -> contour.update(content = it.values)
                is ServerState.Failure -> contour.showError(getString(R.string.err_server_connection))
                else -> contour.showLoading()
            }
        }

//        collect(viewModel.activeServer) {
//            when (it) {
//                is ActiveServerState.None -> contour.update(activerServer = null)
//                is ActiveServerState.Value -> contour.update(activerServer = it.serverInfo)
//                else -> contour.showLoading()
//            }
//        }

        collect(viewModel.searchState) {
            contour.btnSearchEnabled(it.isValid)
        }
    }

    override fun observeCommands() {
        collect(viewModel.oneshotEvents) { event ->
            when (event) {
                is RadioBrowserEvent.LanguageList -> findNavController().navigate(
                    R.id.action_radioBrowser_to_languageList,
                    Bundle().apply { putString(NavHelper.title, getString(R.string.nav_label_languages)) }
                )
                is RadioBrowserEvent.TagList -> findNavController().navigate(
                    R.id.action_radioBrowser_to_languageList,
                    Bundle().apply { putString("title", getString(R.string.nav_label_tags)) }
                )
                is RadioBrowserEvent.TopVoted -> findNavController().navigate(
                    R.id.action_radioBrowser_to_stationList,
                    Bundle().apply { putString("title", getString(R.string.nav_label_topvoted)) }
                )
                is RadioBrowserEvent.Search -> findNavController().navigate(
                    R.id.action_radioBrowser_to_stationList,
                    Bundle().apply { putString("title", getString(R.string.nav_label_search)) }
                )
            }
        }
    }

    private val adapterListener: ServerClick = {
        viewModel.setServer(it)
        contour.serverListCollapse()
    }
}
