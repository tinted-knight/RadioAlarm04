package com.noomit.radioalarm02.di.managers

import com.noomit.domain.alarm_manager.AlarmManager
import com.noomit.domain.alarm_manager.AlarmManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class AlarmManagerModule {
    @Binds
    abstract fun provideAlarmManager(manager: AlarmManagerImpl): AlarmManager
}
