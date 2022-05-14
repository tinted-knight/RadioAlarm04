package com.noomit.radioalarm02.di.managers

import com.noomit.domain.alarm_manager.DismissAlarmManager
import com.noomit.domain.alarm_manager.DismissAlarmManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DismissAlarmManagerModule {
  @Binds
  abstract fun provideDismissAlarmManager(manager: DismissAlarmManagerImpl): DismissAlarmManager
}
