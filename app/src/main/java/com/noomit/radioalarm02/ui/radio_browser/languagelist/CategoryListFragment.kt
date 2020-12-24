package com.noomit.radioalarm02.ui.radio_browser.languagelist

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.ContourFragment
import com.noomit.radioalarm02.base.collect
import com.noomit.radioalarm02.data.CategoryModel
import com.noomit.radioalarm02.domain.language_manager.CategoryManagerState
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.common.textFlow
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@FlowPreview
class CategoryListFragment : ContourFragment<ICategoryLayout>() {

    private val viewModel: RadioBrowserViewModel by navGraphViewModels(R.id.nav_radio_browser)

    override val layout: View
        get() = CategoryListLayout(requireContext())

    override val contour: ICategoryLayout
        get() = view as ICategoryLayout

    private val categoryClick = { model: CategoryModel ->
        viewModel.showStations(model)
        findNavController().navigate(
            R.id.action_languageList_to_stationList,
            Bundle().apply { putString("title", model.name) }
        )
    }

    private val adapter by lazy(LazyThreadSafetyMode.NONE) { LanguageListAdapter(categoryClick) }

    override fun prepareView() {
        contour.apply {
            setAdapter(adapter)
            showLoading()
        }
    }

    override fun observeViewModel() {
        collect(viewModel.categoryList) {
            when (it) {
                is CategoryManagerState.Loading -> contour.showLoading()
                is CategoryManagerState.Empty -> contour.showContent(emptyList())
                is CategoryManagerState.Values -> contour.showContent(it.values)
                is CategoryManagerState.Failure -> requireContext().toast(it.e.localizedMessage)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_searchview, menu)
        val searchItem = menu.findItem(R.id.action_search)
        if (searchItem != null) {
            val searchView = searchItem.actionView as SearchView
            searchView.setOnCloseListener { false }
            viewModel.applyCategoryFilter(searchView.textFlow(lifecycleScope).debounce(500))
        }
        super.onCreateOptionsMenu(menu, inflater)
    }
}
