package com.noomit.radioalarm02.di.storage

import com.noomit.db.AppDatabase
import com.noomit.domain.AlarmQueries
import com.noomit.domain.FavoriteQueries
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class RepositoryModule {

    @Provides
    fun provideFavoriteQueries(database: AppDatabase): FavoriteQueries {
        return database.favoriteQueries
    }

    @Provides
    fun provideAlarmQueries(database: AppDatabase): AlarmQueries {
        return database.alarmQueries
    }
}
