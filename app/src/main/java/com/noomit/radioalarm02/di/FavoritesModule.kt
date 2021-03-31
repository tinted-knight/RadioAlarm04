package com.noomit.radioalarm02.di

import com.noomit.db.AppDatabase
import com.noomit.domain.FavoriteQueries
import com.noomit.domain.favorites_manager.FavoritesManager
import com.noomit.domain.favorites_manager.FavoritesManagerContract
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class FavoritesModule {
    @Provides
    fun provideFavoriteManager(queries: FavoriteQueries): FavoritesManagerContract {
        return FavoritesManager(queries)
    }

    @Provides
    fun provideFavoriteQueries(database: AppDatabase): FavoriteQueries {
        return database.favoriteQueries
    }
}