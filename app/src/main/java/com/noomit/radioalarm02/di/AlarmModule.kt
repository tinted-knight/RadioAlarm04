package com.noomit.radioalarm02.di

import com.noomit.domain.alarm_manager.AlarmScheduler
import com.noomit.domain.alarm_manager.alarm_composer.AlarmComposer
import com.noomit.domain.alarm_manager.alarm_composer.AlarmComposerImpl
import com.noomit.radioalarm02.util.AlarmSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class AlarmModule {

  @Binds
  abstract fun provideAlarmScheduler(alarmScheduler: AlarmSchedulerImpl): AlarmScheduler

  @Binds
  abstract fun provideAlarmComposer(alarmComposer: AlarmComposerImpl): AlarmComposer
}
