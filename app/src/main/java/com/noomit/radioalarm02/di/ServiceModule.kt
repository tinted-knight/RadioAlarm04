package com.noomit.radioalarm02.di

import com.noomit.radioalarm02.domain.alarm_manager.AlarmManager
import com.noomit.radioalarm02.domain.alarm_manager.AlarmManagerContract
import com.noomit.radioalarm02.domain.favorite_manager.FavoritesManager
import com.noomit.radioalarm02.domain.favorite_manager.IFavoritesManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@InstallIn(ActivityComponent::class)
@Module
abstract class ServiceModule {

    @Binds
    abstract fun bindFavoritesManager(manager: FavoritesManager): IFavoritesManager

    @Binds
    abstract fun bindAlarmManager(manager: AlarmManager): AlarmManagerContract
}
