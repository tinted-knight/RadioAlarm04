package com.noomit.radioalarm02.radiobrowserview.ui

import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.BaseFragment
import com.noomit.radioalarm02.databinding.FragmentLanguageListBinding
import com.noomit.radioalarm02.radiobrowserview.adapters.LanguageListAdapter
import com.noomit.radioalarm02.radiobrowserview.viewmodels.Action
import com.noomit.radioalarm02.radiobrowserview.viewmodels.RadioBrowserViewModel
import com.noomit.radioalarm02.radiobrowserview.viewmodels.categories.LanguageList
import com.noomit.radioalarm02.radiobrowserview.viewmodels.categories.LanguageManagerState
import com.noomit.radioalarm02.toast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect

@ExperimentalCoroutinesApi
class LanguageListFragment : BaseFragment(R.layout.fragment_language_list) {

    private val viewModel: RadioBrowserViewModel by activityViewModels()

    override val viewBinding: FragmentLanguageListBinding by viewBinding()

    override fun prepareUi() {
        showLoading()
        viewBinding.rvCategoryList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            isVerticalScrollBarEnabled = true
            adapter = LanguageListAdapter { value ->
                viewModel.offer(Action.Show.StationsByLanguage(value))
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
