package com.noomit.radioalarm02.ui.radio_browser.languagelist

import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.SearchView
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.noomit.domain.category_manager.CategoryManagerState
import com.noomit.domain.entities.CategoryModel
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.common.textFlow
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserViewModel
import com.noomit.radioalarm02.util.fragment.ContourFragment
import com.noomit.radioalarm02.util.fragment.collect
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

@FlowPreview
@AndroidEntryPoint
class CategoryListFragment : ContourFragment<ICategoryLayout>() {

  private val viewModel: RadioBrowserViewModel by hiltNavGraphViewModels(R.id.nav_radio_browser)

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

  private var recyclerState: Parcelable? = null

  override fun prepareView(savedState: Bundle?) {
    contour.apply {
      val adapter = LanguageListAdapter(categoryClick)
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
        // #todo smth like showError or empty
        else -> contour.showLoading()
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

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.menu_searchview, menu)
    val searchItem = menu.findItem(R.id.action_search)
    if (searchItem != null) {
      val searchView = searchItem.actionView as SearchView
      searchView.setOnCloseListener { false }
      viewModel.applyCategoryFilter(searchView.textFlow(lifecycleScope))
    }
    super.onCreateOptionsMenu(menu, inflater)
  }
}
