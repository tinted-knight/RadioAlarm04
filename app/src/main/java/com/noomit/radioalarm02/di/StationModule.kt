package com.noomit.radioalarm02.di

import com.noomit.domain.RadioBrowserContract
import com.noomit.domain.station_manager.StationManager
import com.noomit.domain.station_manager.StationManagerContract
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class StationModule {
    @Provides
    fun provideStationManager(apiService: RadioBrowserContract): StationManagerContract {
        return StationManager(apiService)
    }
}