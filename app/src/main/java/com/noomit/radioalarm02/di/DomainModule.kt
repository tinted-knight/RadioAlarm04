package com.noomit.radioalarm02.di

import com.example.radiobrowser.RadioBrowserService
import com.noomit.domain.server_manager.ServerManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@InstallIn(ApplicationComponent::class)
@Module
class DomainModule {

    @Provides
    fun provideServerManager(apiService: RadioBrowserService): ServerManager {
        return ServerManager(apiService)
    }
}
