package com.noomit.radioalarm02.radiobrowserview.viewmodels.categories

import com.noomit.radioalarm02.model.CategoryModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

sealed class LanguageManagerState {
    object Loading : LanguageManagerState()
    object Empty : LanguageManagerState()
    data class Values(val values: LanguageList) : LanguageManagerState()
    data class Failure(val e: Throwable) : LanguageManagerState()
}

@ExperimentalCoroutinesApi
interface CategoryManager<Model : CategoryModel, State, ChosenState> {
    val chosenCategory: Flow<ChosenState>
    val state: StateFlow<State>

    fun onCategoryChoosed(value: Model)
}
