package com.noomit.domain.entities

data class StationNetworkEntity(
  val stationuuid: String,
  val name: String,
  val url: String,
  val url_resolved: String,
  val favicon: String,
  val codec: String,
  val bitrate: String,
  val country: String,
  val homepage: String,
  val tags: String,
  val votes: Int,
)

data class CategoryNetworkEntity(
  val name: String,
  val stationcount: Int,
)

data class SearchRequest(
  val name: String,
  val tag: String,
)
