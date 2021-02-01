package com.noomit.radioalarm02.di

import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.database.getDatabase
import com.noomit.db.AppDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
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
}
