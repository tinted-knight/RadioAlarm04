package com.noomit.radioalarm02.di

import android.content.Context
import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.database.getDatabase
import com.noomit.db.AppDatabase
import com.noomit.domain.alarm_manager.ScheduleAlarmUtilsContract
import com.noomit.radioalarm02.util.ScheduleAlarmUtils
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun app2Database(driver: AndroidSqliteDriver): AppDatabase {
        return getDatabase(driver)
    }

    @Provides
    @Singleton
    fun apiService(): RadioBrowserService {
        return RadioBrowserService()
    }

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): ScheduleAlarmUtilsContract {
        return ScheduleAlarmUtils(context)
    }
}
