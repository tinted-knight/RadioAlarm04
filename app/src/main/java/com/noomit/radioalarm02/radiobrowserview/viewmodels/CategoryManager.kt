package com.noomit.radioalarm02.radiobrowserview.viewmodels

import androidx.lifecycle.MutableLiveData
import com.noomit.radioalarm02.model.CategoryModel
import kotlinx.coroutines.flow.Flow
import timber.log.Timber

// #todo to base package
interface WithLogTag {
    val logTag: String

    fun plog(message: String) = Timber.tag(logTag).i("$message [${Thread.currentThread().name}]")
}

typealias CategoryList<Model> = List<Model>
typealias CategoryListResponse<Model> = Result<CategoryList<Model>>

interface CategoryManager<Model : CategoryModel> : WithLogTag {

    val chosenCategory: MutableLiveData<Model>

    val chosenFlow: Flow<Model?>

    fun onCategoryChoosed(value: Model)

    val categoryFlow: Flow<CategoryListResponse<Model>>
}
