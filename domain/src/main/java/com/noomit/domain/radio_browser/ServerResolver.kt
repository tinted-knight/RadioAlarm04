package com.noomit.domain.radio_browser

import kotlinx.coroutines.flow.Flow

interface ServerResolver {
    val cached: Flow<ServerListResponse>
    suspend fun checkAlive()
}