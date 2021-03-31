package com.noomit.radioalarm02.di

import com.noomit.db.AppDatabase
import com.noomit.domain.AlarmQueries
import com.noomit.domain.FavoriteQueries
import com.noomit.domain.RadioBrowserContract
import com.noomit.domain.alarm_manager.AlarmManager
import com.noomit.domain.alarm_manager.AlarmManagerContract
import com.noomit.domain.alarm_manager.ScheduleAlarmUtilsContract
import com.noomit.domain.category_manager.CategoryManager
import com.noomit.domain.category_manager.CategoryManagerContract
import com.noomit.domain.favorites_manager.FavoritesManager
import com.noomit.domain.favorites_manager.FavoritesManagerContract
import com.noomit.domain.station_manager.StationManager
import com.noomit.domain.station_manager.StationManagerContract
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class DomainModule {

    @Provides
    fun provideCategoryManager(apiService: RadioBrowserContract): CategoryManagerContract {
        return CategoryManager(apiService)
    }

    @Provides
    fun provideStationManager(apiService: RadioBrowserContract): StationManagerContract {
        return StationManager(apiService)
    }

    @Provides
    fun provideAlarmManager(queries: AlarmQueries, scheduler: ScheduleAlarmUtilsContract)
            : AlarmManagerContract {
        return AlarmManager(queries, scheduler)
    }

    @Provides
    fun provideFavoriteManager(queries: FavoriteQueries): FavoritesManagerContract {
        return FavoritesManager(queries)
    }

    @Provides
    fun provideFavoriteQueries(database: AppDatabase): FavoriteQueries {
        return database.favoriteQueries
    }

    @Provides
    fun provideAlarmQueries(database: AppDatabase): AlarmQueries {
        return database.alarmQueries
    }
}
