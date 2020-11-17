package com.noomit.radioalarm02.domain

import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.domain.language_manager.LanguageManager
import com.noomit.radioalarm02.domain.server_manager.ServerManager
import com.noomit.radioalarm02.domain.station_manager.StationManager

interface IServiceProvider {
    val serverManager: ServerManager
    val languageManager: LanguageManager
    val stationManager: StationManager
}

class ServiceProvider(apiService: RadioBrowserService) : IServiceProvider {
    override val serverManager = ServerManager(apiService)
    override val languageManager = LanguageManager(apiService)
    override val stationManager = StationManager(apiService)
}
