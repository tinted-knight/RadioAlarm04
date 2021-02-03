package com.noomit.domain.category_manager

import com.noomit.domain.entities.CategoryModel
import kotlinx.coroutines.flow.Flow

interface CategoryManagerContract {
    val state: Flow<CategoryManagerState>
    suspend fun getLanguages()
    suspend fun getTags()
}

sealed class CategoryManagerState {
    object Loading : CategoryManagerState()
    object Empty : CategoryManagerState()
    data class Values(val values: List<CategoryModel>) : CategoryManagerState()
    data class Failure(val e: Throwable) : CategoryManagerState()
}
