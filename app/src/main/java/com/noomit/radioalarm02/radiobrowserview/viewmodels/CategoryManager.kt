package com.noomit.radioalarm02.radiobrowserview.viewmodels

import androidx.lifecycle.LiveData
import com.noomit.radioalarm02.model.CategoryModel
import timber.log.Timber

interface WithLogTag {
    val logTag: String

    fun plog(message: String) = Timber.tag(logTag).i("$message [${Thread.currentThread().name}]")
}

typealias CategoryList<Model> = List<Model>
typealias CategoryListResponse<Model> = Result<CategoryList<Model>>

interface CategoryManager<Model : CategoryModel> {
    val chosenCategory: LiveData<Model>
    val values: LiveData<CategoryListResponse<Model>>

    fun onCategoryChoosed(value: Model)
}
