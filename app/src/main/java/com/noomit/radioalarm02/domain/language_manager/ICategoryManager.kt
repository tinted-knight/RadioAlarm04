package com.noomit.radioalarm02.domain.language_manager

import com.noomit.radioalarm02.model.CategoryModel
import kotlinx.coroutines.flow.StateFlow

interface ICategoryManager<Model : CategoryModel, State, ChosenState> {
    //    val chosenCategory: Flow<ChosenState>
    val state: StateFlow<State>

//    fun onCategoryChoosed(value: Model)
}
