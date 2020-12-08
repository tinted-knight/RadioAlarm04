package com.noomit.radioalarm02.domain.language_manager

import com.noomit.radioalarm02.data.CategoryModel

sealed class CategoryManagerState {
    object Loading : CategoryManagerState()
    object Empty : CategoryManagerState()
    data class Values(val values: List<CategoryModel>) : CategoryManagerState()
    data class Failure(val e: Throwable) : CategoryManagerState()
}
