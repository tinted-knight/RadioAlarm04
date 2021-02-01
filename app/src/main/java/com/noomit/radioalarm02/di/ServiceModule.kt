package com.noomit.radioalarm02.di

import com.example.radiobrowser.RadioBrowserService
import com.noomit.domain.RadioBrowserContract
import com.noomit.domain.server_manager.ServerManager
import com.noomit.domain.server_manager.ServerManagerContract
import com.noomit.radioalarm02.domain.alarm_manager.AlarmManager
import com.noomit.radioalarm02.domain.alarm_manager.AlarmManagerContract
import com.noomit.radioalarm02.domain.alarm_manager.FiredAlarmManagerContract
import com.noomit.radioalarm02.domain.favorites_manager.FavoritesManager
import com.noomit.radioalarm02.domain.favorites_manager.FavoritesManagerContract
import com.noomit.radioalarm02.domain.language_manager.CategoryManager
import com.noomit.radioalarm02.domain.language_manager.CategoryManagerContract
import com.noomit.radioalarm02.domain.station_manager.StationManager
import com.noomit.radioalarm02.domain.station_manager.StationManagerContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
abstract class ServiceModule {

    @Binds
    abstract fun bindFavoritesManager(manager: FavoritesManager): FavoritesManagerContract

    @Binds
    abstract fun bindAlarmManager(manager: AlarmManager): AlarmManagerContract

    @Binds
    abstract fun bindFiredAlarmManager(manager: AlarmManager): FiredAlarmManagerContract

    @Binds
    abstract fun bindCategoryManager(manager: CategoryManager): CategoryManagerContract

    @Binds
    abstract fun bindServerManager(manager: ServerManager): ServerManagerContract

    @Binds
    abstract fun bindStationManager(manager: StationManager): StationManagerContract

    @Binds
    abstract fun bindRadioBrowserService(service: RadioBrowserService): RadioBrowserContract
}
