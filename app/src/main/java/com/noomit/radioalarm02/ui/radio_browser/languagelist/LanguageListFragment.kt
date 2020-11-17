package com.noomit.radioalarm02.ui.radio_browser.languagelist

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.BaseFragment
import com.noomit.radioalarm02.databinding.FragmentLanguageListBinding
import com.noomit.radioalarm02.domain.language_manager.LanguageList
import com.noomit.radioalarm02.domain.language_manager.LanguageManagerState
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.radio_browser.Action
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class LanguageListFragment : BaseFragment(R.layout.fragment_language_list) {

    private val viewModel: RadioBrowserViewModel by navGraphViewModels(R.id.nav_radio_browser)

    override val viewBinding: FragmentLanguageListBinding by viewBinding()

    override fun prepareUi() {
        showLoading()
        viewBinding.rvCategoryList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            isVerticalScrollBarEnabled = true
            adapter = LanguageListAdapter { value ->
                viewModel.offer(Action.Click.StationsByLanguage(value))
                findNavController().navigate(R.id.action_languageList_to_stationList)
            }
            // #todo LanguageList restore state
//            layoutManager?.onRestoreInstanceState()
        }
    }

    override fun listenUiEvents() {
    }

    override fun observeModel() {
        lifecycleScope.launchWhenStarted {
            viewModel.languageList.collect {
                when (it) {
                    is LanguageManagerState.Loading -> showLoading()
                    is LanguageManagerState.Empty -> showContent(emptyList())
                    is LanguageManagerState.Values -> showContent(it.values)
                    is LanguageManagerState.Failure -> requireContext().toast(it.e.localizedMessage)
                }
            }
        }
    }

    private fun showContent(values: LanguageList) = with(viewBinding) {
        progressIndicator.visibility = View.GONE
        (rvCategoryList.adapter as LanguageListAdapter).submitList(values)
        rvCategoryList.visibility = View.VISIBLE
    }

    private fun showLoading() = with(viewBinding) {
        progressIndicator.visibility = View.VISIBLE
        rvCategoryList.visibility = View.INVISIBLE
    }
}
