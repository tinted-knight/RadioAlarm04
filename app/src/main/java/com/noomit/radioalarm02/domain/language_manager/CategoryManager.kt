package com.noomit.radioalarm02.domain.language_manager

import com.example.radiobrowser.CategoryNetworkEntity
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.base.WithLogTag
import com.noomit.radioalarm02.data.CategoryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class CategoryManager @Inject constructor(
    private val apiService: RadioBrowserService,
) : CategoryManagerContract, WithLogTag {

    override val logTag = "lang_manager"

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
            .catch { e ->
                plog("LanguageManager.catch: ${e.localizedMessage}")
                _state.value = CategoryManagerState.Failure(e)
            }
            .collect {
                _state.value = when {
                    it.isEmpty() -> CategoryManagerState.Empty
                    else -> CategoryManagerState.Values(it)
                }
            }
    }
}
