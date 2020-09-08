package com.noomit.radioalarm02.radiobrowserview.model

data class LanguageModel(
    val name: String,
    val stationCount: String,
)

data class StationModel(
    val name: String,
    val upvotes: String,
    val streamUrl: String,
)