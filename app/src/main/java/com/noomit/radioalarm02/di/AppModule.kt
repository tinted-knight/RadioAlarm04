package com.noomit.radioalarm02.di

import android.content.Context
import com.example.radiobrowser.Database
import com.example.radiobrowser.RadioBrowserService
import com.example.radiobrowser.database.AppDatabase
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
    fun appDatabase(@ApplicationContext appContext: Context): Database {
        return AppDatabase.getInstance(appContext)
    }

    @Provides
    @Singleton
    fun apiService(): RadioBrowserService {
        return RadioBrowserService()
    }
}
