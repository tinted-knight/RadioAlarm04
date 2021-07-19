package com.noomit.radioalarm02.di

import com.noomit.db.AppDatabase
import com.noomit.domain.AlarmQueries
import com.noomit.domain.FavoriteQueries
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
class DatabaseModule {

    @Provides
    @ViewModelScoped
    fun provideFavoriteQueries(database: AppDatabase): FavoriteQueries {
        return database.favoriteQueries
    }

    @Provides
    @ViewModelScoped
    fun provideAlarmQueries(database: AppDatabase): AlarmQueries {
        return database.alarmQueries
    }
}
