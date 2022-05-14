package com.noomit.radioalarm02.di.managers

import com.noomit.domain.favorites_manager.FavoritesManager
import com.noomit.domain.favorites_manager.FavoritesManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class FavoritesManagerModule {
  @Binds
  abstract fun bindFavoriteManager(manager: FavoritesManagerImpl): FavoritesManager
}
