package com.noomit.radioalarm02.di

import android.content.Context
import com.noomit.db.AppDatabase
import com.noomit.domain.AlarmQueries
import com.noomit.domain.alarm_manager.AlarmManager
import com.noomit.domain.alarm_manager.AlarmManagerContract
import com.noomit.domain.alarm_manager.ScheduleAlarmUtilsContract
import com.noomit.radioalarm02.util.ScheduleAlarmUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ViewModelComponent::class)
class AlarmModule {
    @Provides
    fun provideAlarmManager(queries: AlarmQueries, scheduler: ScheduleAlarmUtilsContract)
            : AlarmManagerContract {
        return AlarmManager(queries, scheduler)
    }

    @Provides
    fun provideAlarmQueries(database: AppDatabase): AlarmQueries {
        return database.alarmQueries
    }

    @Provides
    fun provideAlarmScheduler(@ApplicationContext context: Context): ScheduleAlarmUtilsContract {
        return ScheduleAlarmUtils(context)
    }
}