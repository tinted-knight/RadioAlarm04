package com.noomit.radioalarm02.radiobrowserview.ui

import android.view.View
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.RadioVMFragment
import com.noomit.radioalarm02.databinding.FragmentLanguageListBinding
import com.noomit.radioalarm02.radiobrowserview.LanguageList
import com.noomit.radioalarm02.radiobrowserview.adapters.CategoryListAdapter

class LanguageListFragment : RadioVMFragment(R.layout.fragment_language_list) {

    private val viewBinding: FragmentLanguageListBinding by viewBinding()

    override fun prepareUi() {
        showLoading()
        viewBinding.rvCategoryList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            isVerticalScrollBarEnabled = true
            adapter = CategoryListAdapter { value ->
                viewModel.onLanguageChoosed(value)
                findNavController().navigate(R.id.action_languageList_to_stationList)
            }
            // #todo LanguageList restore state
//            layoutManager?.onRestoreInstanceState()
        }
    }

    override fun listenUiEvents() {
    }

    override fun observeModel() = with(viewModel) {
        languageList.observe(viewLifecycleOwner) {
            it.fold(
                onSuccess = { languageList ->
                    showContent(languageList)
                },
                onFailure = {},
            )
        }
    }

    private fun showContent(values: LanguageList) = with(viewBinding) {
        progressIndicator.visibility = View.GONE
        (rvCategoryList.adapter as CategoryListAdapter).submitList(values)
        rvCategoryList.visibility = View.VISIBLE
    }

    private fun showLoading() = with(viewBinding) {
        progressIndicator.visibility = View.VISIBLE
        rvCategoryList.visibility = View.INVISIBLE
    }
}