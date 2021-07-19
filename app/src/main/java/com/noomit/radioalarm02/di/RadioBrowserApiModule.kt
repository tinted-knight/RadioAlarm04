package com.noomit.radioalarm02.di

import com.noomit.data.remote.ApiFactory
import com.noomit.data.remote.ApiFactoryContract
import com.noomit.data.remote.RadioBrowserImpl
import com.noomit.domain.radio_browser.RadioBrowser
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RadioBrowserApiModule {

    @Binds
    @Singleton
    abstract fun bindRadioBrowserService(service: RadioBrowserImpl): RadioBrowser

    @Binds
    @Singleton
    abstract fun bindApi(factory: ApiFactory): ApiFactoryContract
}
