package com.noomit.radioalarm02.model

data class LanguageModel(
    val name: String,
    val stationCount: String,
)

data class StationModel(
    val name: String,
    val upvotes: String,
    val streamUrl: String,
    val country: String,
    val homepage: String,
    val codec: String,
    val bitrate: String,
    val favicon: String,
    val tags: String,
)