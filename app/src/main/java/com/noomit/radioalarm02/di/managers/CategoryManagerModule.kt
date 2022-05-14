package com.noomit.radioalarm02.di.managers

import com.noomit.domain.category_manager.CategoryManager
import com.noomit.domain.category_manager.CategoryManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class CategoryManagerModule {
  @Binds
  abstract fun bindCategoryManager(manager: CategoryManagerImpl): CategoryManager
}
