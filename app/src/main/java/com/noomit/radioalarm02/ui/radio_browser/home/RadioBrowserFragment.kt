package com.noomit.radioalarm02.ui.radio_browser.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.noomit.radioalarm02.Application00
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.ViewModelFactory
import com.noomit.radioalarm02.domain.server_manager.ServerState
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.radio_browser.Action
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserViewModel
import com.squareup.contour.utils.children
import kotlinx.coroutines.flow.collect
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app").i("$message [${Thread.currentThread().name}]")

//class RadioBrowserFragment() : BaseFragment(R.layout.fragment_radio_browser) {
class RadioBrowserFragment() : Fragment() {

//    override val viewBinding: FragmentRadioBrowserBinding by viewBinding()

    private val viewModel: RadioBrowserViewModel by navGraphViewModels(
        navGraphId = R.id.nav_radio_browser,
        factoryProducer = { ViewModelFactory(requireActivity().application as Application00) }
    )

    private val listener = object : RadioBrowserHomeDelegate {
        override fun onLanguageClick() {
            viewModel.offer(Action.Click.LanguageList)
            findNavController().navigate(R.id.action_radioBrowser_to_languageList)
        }
    }

    private val adapter by lazy(LazyThreadSafetyMode.NONE) { ServerListAdapter(viewModel::setServer) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val scrollView = ScrollView(context)
        scrollView.addView(RadioBrowserHomeLayout(requireContext()))
        scrollView.isVerticalScrollBarEnabled = true

        return scrollView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val view = ((view as ViewGroup).children.first() as IRadioBrowserHomeLayout).also {
            it.delegate = listener
            it.setServerAdapter(adapter)
            it.showLoading()
        }

        lifecycleScope.launchWhenStarted {
            viewModel.availableServers.collect {
                when (it) {
                    is ServerState.Loading -> {
                    }
                    is ServerState.Values -> {
                        plog("server list")
                        view.showContent(it.values)
                    }
                    is ServerState.Failure -> requireContext().toast(it.e.localizedMessage)
                }
            }
        }
    }

//    override fun prepareUi() {
//        showLoading()
//        viewBinding.rvServers.apply {
//            setHasFixedSize(true)
//            layoutManager = LinearLayoutManager(requireContext())
//            isVerticalScrollBarEnabled = true
//            adapter = ServerListAdapter(
//                onServerClick = { serverInfo ->
//                    viewModel.setServer(serverInfo)
//                }
//            )
//            // #todo StationList restore state
////            layoutManager?.onRestoreInstanceState()
//        }
//    }
//
//    override fun observeModel() {
//        lifecycleScope.launchWhenStarted {
//            viewModel.availableServers.collect {
//                when (it) {
//                    is ServerState.Loading -> showLoading()
//                    is ServerState.Values -> showContent(it.values)
//                    is ServerState.Failure -> requireContext().toast(it.e.localizedMessage)
//                }
//            }
//        }
//    }
//
//    override fun listenUiEvents() = with(viewBinding) {
//        btnLanguages.setOnClickListener {
//            viewModel.offer(Action.Click.LanguageList)
//            findNavController().navigate(R.id.action_radioBrowser_to_languageList)
//        }
//    }
//
//    private fun showContent(values: List<ServerInfo>) = with(viewBinding) {
//        (rvServers.adapter as ServerListAdapter).submitList(values)
//        rvServers.viewShow()
//        progressIndicator.viewHide()
//        btnLanguages.isEnabled = true
//        btnTags.isEnabled = true
//        btnTopVoted.isEnabled = true
//        btnAllStations.isEnabled = true
//
//        btnSearch.isEnabled = true
//        etSearchName.isEnabled = true
//        etSearchTag.isEnabled = true
//    }
//
//    private fun showLoading() = with(viewBinding) {
//        progressIndicator.viewShow()
//        rvServers.viewHide()
//        btnLanguages.isEnabled = false
//        btnTags.isEnabled = false
//        btnTopVoted.isEnabled = false
//        btnAllStations.isEnabled = false
//
//        btnSearch.isEnabled = false
//        etSearchName.isEnabled = false
//        etSearchTag.isEnabled = false
//    }
}
