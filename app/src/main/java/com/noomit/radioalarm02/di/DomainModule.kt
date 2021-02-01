package com.noomit.radioalarm02.di

import android.content.Context
import com.noomit.data.database.getAndroidSqlDriver
import com.noomit.data.remote.RadioBrowserService
import com.noomit.db.AppDatabase
import com.noomit.domain.AlarmQueries
import com.noomit.domain.FavoriteQueries
import com.noomit.domain.alarm_manager.AlarmManager
import com.noomit.domain.alarm_manager.ScheduleAlarmUtilsContract
import com.noomit.domain.category_manager.CategoryManager
import com.noomit.domain.favorites_manager.FavoritesManager
import com.noomit.domain.server_manager.ServerManager
import com.noomit.domain.station_manager.StationManager
import com.squareup.sqldelight.android.AndroidSqliteDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ApplicationComponent::class)
@Module
class DomainModule {

    @Provides
    fun provideServerManager(apiService: RadioBrowserService): ServerManager {
        return ServerManager(apiService)
    }

    @Provides
    fun provideCategoryManager(apiService: RadioBrowserService): CategoryManager {
        return CategoryManager(apiService)
    }

    @Provides
    fun provideStationManager(apiService: RadioBrowserService): StationManager {
        return StationManager(apiService)
    }

    @Provides
    fun provideAlarmManager(queries: AlarmQueries, scheduler: ScheduleAlarmUtilsContract): AlarmManager {
        return AlarmManager(queries, scheduler)
    }

    @Provides
    fun provideFavoriteManager(queries: FavoriteQueries): FavoritesManager {
        return FavoritesManager(queries)
    }

    @Provides
    fun provideSqlDriver(@ApplicationContext appContext: Context): AndroidSqliteDriver {
        return getAndroidSqlDriver(appContext)
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
