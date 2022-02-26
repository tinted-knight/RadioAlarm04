package com.noomit.domain.radio_browser

sealed class ActiveServerState {
  object None : ActiveServerState()
  object Loading : ActiveServerState()
  data class Value(val serverInfo: ServerInfo) : ActiveServerState()
}

sealed class ServerListResponse {
  enum class ServerListFailure { UnknownHost, NetworkError, NoReachableServers }

  class Success(val value: List<ServerInfo>) : ServerListResponse()
  class Failure(val error: ServerListFailure) : ServerListResponse()
  object Loading : ServerListResponse()
}

data class ServerInfo(val urlString: String, val isReachable: Boolean)
