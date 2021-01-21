package com.noomit.radioalarm02.ui.radio_browser.languagelist

import android.os.Bundle
import android.os.Parcelable
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@FlowPreview
@AndroidEntryPoint
class CategoryListFragment : ContourFragment<ICategoryLayout>() {

    private val viewModel: RadioBrowserViewModel by navGraphViewModels(R.id.nav_radio_browser) {
        defaultViewModelProviderFactory
    }

    override val layout: View
        get() = CategoryListLayout(requireContext())

    override val contour: ICategoryLayout
        get() = view as ICategoryLayout

    private val categoryClick = { model: CategoryModel ->
        viewModel.requestCategory(model)
        findNavController().navigate(
            R.id.action_languageList_to_stationList,
            Bundle().apply { putString("title", model.name) }
        )
    }

    private val adapter by lazy(LazyThreadSafetyMode.NONE) { LanguageListAdapter(categoryClick) }

    private var recyclerState: Parcelable? = null

    override fun prepareView(savedState: Bundle?) {
        contour.apply {
            setAdapter(adapter)
            showLoading()
            recyclerState?.let {
                contour.setRecyclerState(it)
                return@apply
            }
            savedState?.let { bundle ->
                bundle.getParcelable<Parcelable>(RECYCLER_STATE)?.let { state ->
                    contour.setRecyclerState(state)
                }
            }
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

    override fun onPause() {
        val state = contour.getRecyclerState()
        recyclerState = state
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(RECYCLER_STATE, recyclerState)
        super.onSaveInstanceState(outState)
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
