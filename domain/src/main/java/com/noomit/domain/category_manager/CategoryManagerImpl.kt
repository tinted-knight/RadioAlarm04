package com.noomit.domain.category_manager

import com.noomit.domain.entities.CategoryModel
import com.noomit.domain.entities.CategoryNetworkEntity
import com.noomit.domain.radio_browser.RadioBrowser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class CategoryManagerImpl @Inject constructor(private val apiService: RadioBrowser) :
  CategoryManager {

  private val _state = MutableStateFlow<CategoryManagerState>(CategoryManagerState.Loading)
  override val state = _state

  override suspend fun getLanguages() = getCategory(apiService::getLanguageList) {
    CategoryModel.Language(it.name, it.stationcount.toString())
  }

  override suspend fun getTags() = getCategory(apiService::getTagList) {
    CategoryModel.Tag(it.name, it.stationcount.toString())
  }

  private suspend fun getCategory(
    apiMethod: suspend () -> List<CategoryNetworkEntity>,
    mapper: (CategoryNetworkEntity) -> CategoryModel,
  ) {
    flow { emit(apiMethod()) }
      .onStart { _state.value = CategoryManagerState.Loading }
      .flowOn(Dispatchers.IO)
      .map { categoryList ->
        categoryList.sortedByDescending { it.stationcount }
          .map(mapper)
      }
      .flowOn(Dispatchers.Default)
      .catch { e -> _state.value = CategoryManagerState.Failure(e) }
      .collect {
        _state.value = when {
          it.isEmpty() -> CategoryManagerState.Empty
          else -> CategoryManagerState.Values(it)
        }
      }
  }
}
