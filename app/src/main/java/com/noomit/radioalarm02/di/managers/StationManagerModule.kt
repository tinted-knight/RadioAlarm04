package com.noomit.radioalarm02.di.managers

import com.noomit.domain.station_manager.StationManager
import com.noomit.domain.station_manager.StationManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class StationManagerModule {
    @Binds
    abstract fun bindStationManager(manager: StationManagerImpl): StationManager
}
