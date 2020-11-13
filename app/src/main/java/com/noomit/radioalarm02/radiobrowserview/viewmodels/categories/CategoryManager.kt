package com.noomit.radioalarm02.radiobrowserview.viewmodels.categories

import com.noomit.radioalarm02.model.CategoryModel
import kotlinx.coroutines.flow.StateFlow

interface CategoryManager<Model : CategoryModel, State, ChosenState> {
    //    val chosenCategory: Flow<ChosenState>
    val state: StateFlow<State>

//    fun onCategoryChoosed(value: Model)
}
