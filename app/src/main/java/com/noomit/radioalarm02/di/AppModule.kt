package com.noomit.radioalarm02.di

import android.content.Context
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.Database
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
    fun appDatabase(@ApplicationContext appContext: Context): Database {
        return Database(
            AndroidSqliteDriver(
                schema = Database.Schema,
                context = appContext,
                name = "favorites.db",
            ),
        )
    }

    @Provides
    @Singleton
    fun apiService(): RadioBrowserService {
        return RadioBrowserService()
    }
}
