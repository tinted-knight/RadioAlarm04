package com.noomit.radioalarm02.ui.radio_browser.home

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.noomit.domain.ActiveServerState
import com.noomit.domain.server_manager.ServerState
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.navigation.NavHelper
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserDirections
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserViewModel
import com.noomit.radioalarm02.util.ContourFragment
import com.noomit.radioalarm02.util.collect
import com.squareup.contour.utils.children
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect

@FlowPreview
@AndroidEntryPoint
class RadioBrowserFragment : ContourFragment<IRadioBrowserHomeLayout>() {

    private val viewModel: RadioBrowserViewModel by navGraphViewModels(R.id.nav_radio_browser) {
        defaultViewModelProviderFactory
    }

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

    override fun prepareView(savedState: Bundle?) {
        contour.apply {
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
                is ServerState.Failure -> requireContext().toast(it.e.localizedMessage)
                // #todo smth like showError or empty
                else -> contour.showLoading()
            }
        }

        collect(viewModel.activeServer) {
            when (it) {
                is ActiveServerState.None -> contour.update(activerServer = null)
                is ActiveServerState.Value -> contour.update(activerServer = it.serverInfo)
                // #todo smth like showError or empty
                else -> contour.showLoading()
            }
        }

        collect(viewModel.searchState) {
            contour.btnSearchEnabled(it.isValid)
        }
    }

    override fun observeCommands() {
        lifecycleScope.launchWhenStarted {
            viewModel.commands.collect { command ->
                when (command) {
                    is RadioBrowserDirections.LanguageList -> findNavController().navigate(
                        R.id.action_radioBrowser_to_languageList,
                        Bundle().apply { putString(NavHelper.title, getString(R.string.nav_label_languages)) }
                    )
                    is RadioBrowserDirections.TagList -> findNavController().navigate(
                        R.id.action_radioBrowser_to_languageList,
                        Bundle().apply { putString("title", getString(R.string.nav_label_tags)) }
                    )
                    is RadioBrowserDirections.TopVoted -> findNavController().navigate(
                        R.id.action_radioBrowser_to_stationList,
                        Bundle().apply { putString("title", getString(R.string.nav_label_topvoted)) }
                    )
                    is RadioBrowserDirections.Search -> findNavController().navigate(
                        R.id.action_radioBrowser_to_stationList,
                        Bundle().apply { putString("title", getString(R.string.nav_label_search)) }
                    )
                }
            }
        }
    }

    private val adapterListener: ServerClick = {
        viewModel.setServer(it)
        contour.serverListCollapse()
    }
}
