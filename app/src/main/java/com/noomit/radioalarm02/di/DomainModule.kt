package com.noomit.radioalarm02.di

import com.noomit.domain.alarm_manager.AlarmManager
import com.noomit.domain.alarm_manager.AlarmManagerImpl
import com.noomit.domain.category_manager.CategoryManager
import com.noomit.domain.category_manager.CategoryManagerImpl
import com.noomit.domain.favorites_manager.FavoritesManager
import com.noomit.domain.favorites_manager.FavoritesManagerImpl
import com.noomit.domain.station_manager.StationManager
import com.noomit.domain.station_manager.StationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class DomainModule {

    @Binds
    @ViewModelScoped
    abstract fun bindCategoryManager(manager: CategoryManagerImpl): CategoryManager

    @Binds
    @ViewModelScoped
    abstract fun bindFavoriteManager(manager: FavoritesManagerImpl): FavoritesManager

    @Binds
    @ViewModelScoped
    abstract fun bindStationManager(manager: StationManagerImpl): StationManager

    @Binds
    @ViewModelScoped
    abstract fun provideAlarmManager(manager: AlarmManagerImpl): AlarmManager
}
