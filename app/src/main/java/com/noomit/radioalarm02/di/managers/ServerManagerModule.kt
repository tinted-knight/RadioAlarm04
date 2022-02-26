package com.noomit.radioalarm02.di.managers

import com.noomit.domain.server_manager.ServerManager
import com.noomit.domain.server_manager.ServerManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ServerManagerModule {

  @Binds
  @Singleton
  abstract fun bindServerManager(manager: ServerManagerImpl): ServerManager
}
