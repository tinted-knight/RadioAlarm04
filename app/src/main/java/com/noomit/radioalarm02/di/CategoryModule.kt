package com.noomit.radioalarm02.di

import com.noomit.domain.RadioBrowserContract
import com.noomit.domain.category_manager.CategoryManager
import com.noomit.domain.category_manager.CategoryManagerContract
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class CategoryModule {
    @Provides
    fun provideCategoryManager(apiService: RadioBrowserContract): CategoryManagerContract {
        return CategoryManager(apiService)
    }
}