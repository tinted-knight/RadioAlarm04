package com.noomit.radioalarm02.domain

import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.domain.favorite_manager.FavoritesManager
import com.noomit.radioalarm02.domain.favorite_manager.IFavoritesManager
import com.noomit.radioalarm02.domain.language_manager.CategoryManager
import com.noomit.radioalarm02.domain.server_manager.ServerManager
import com.noomit.radioalarm02.domain.station_manager.StationManager

interface IServiceProvider {
    val serverManager: ServerManager
    val categoryManager: CategoryManager
    val stationManager: StationManager
    val favoritesManager: IFavoritesManager
}

class ServiceProvider(
    apiService: RadioBrowserService,
    database: Database,
) : IServiceProvider {
    override val serverManager by lazy(LazyThreadSafetyMode.NONE) { ServerManager(apiService) }
    override val categoryManager by lazy(LazyThreadSafetyMode.NONE) { CategoryManager(apiService) }
    override val stationManager by lazy(LazyThreadSafetyMode.NONE) { StationManager(apiService) }
    override val favoritesManager by lazy(LazyThreadSafetyMode.NONE) { FavoritesManager(database) }
}
