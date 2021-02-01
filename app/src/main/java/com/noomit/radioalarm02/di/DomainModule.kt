package com.noomit.radioalarm02.di

import com.example.radiobrowser.RadioBrowserService
import com.noomit.domain.category_manager.CategoryManager
import com.noomit.domain.server_manager.ServerManager
import com.noomit.domain.station_manager.StationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
class DomainModule {

    @Provides
    fun provideServerManager(apiService: RadioBrowserService): ServerManager {
        return ServerManager(apiService)
    }

    @Provides
    fun provideCategoryManager(apiService: RadioBrowserService): CategoryManager {
        return CategoryManager(apiService)
    }

    @Provides
    fun provideStationManager(apiService: RadioBrowserService): StationManager {
        return StationManager(apiService)
    }
}
