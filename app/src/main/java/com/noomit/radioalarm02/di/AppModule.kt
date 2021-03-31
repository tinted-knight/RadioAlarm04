package com.noomit.radioalarm02.di

import android.content.Context
import com.noomit.data.database.getAndroidSqlDriver
import com.noomit.data.database.getDatabase
import com.noomit.data.remote.RadioBrowserService
import com.noomit.db.AppDatabase
import com.noomit.domain.RadioBrowserContract
import com.noomit.domain.server_manager.ServerManager
import com.noomit.domain.server_manager.ServerManagerContract
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun app2Database(driver: AndroidSqliteDriver): AppDatabase {
        return getDatabase(driver)
    }

    @Provides
    @Singleton
    fun apiService(): RadioBrowserContract {
        return RadioBrowserService()
    }

    @Provides
    @Singleton
    fun provideServerManager(apiService: RadioBrowserContract): ServerManagerContract {
        return ServerManager(apiService)
    }

    @Provides
    fun provideSqlDriver(@ApplicationContext appContext: Context): AndroidSqliteDriver {
        return getAndroidSqlDriver(appContext)
    }
}
