package com.noomit.radioalarm02.di

import android.content.Context
import com.noomit.data.database.getAndroidSqlDriver
import com.noomit.data.database.getDatabase
import com.noomit.db.AppDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAppDatabase(driver: AndroidSqliteDriver): AppDatabase {
        return getDatabase(driver)
    }

    @Provides
    @Singleton
    fun provideSqlDriver(@ApplicationContext appContext: Context): AndroidSqliteDriver {
        return getAndroidSqlDriver(appContext)
    }

}
