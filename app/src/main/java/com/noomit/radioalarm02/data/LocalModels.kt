package com.noomit.radioalarm02.data

interface CategoryModel {
    val name: String
    val stationCount: String
}

data class LanguageModel(
    override val name: String,
    override val stationCount: String,
) : CategoryModel

data class TagModel(
    override val name: String,
    override val stationCount: String,
) : CategoryModel

data class StationModel(
    val name: String,
    val upvotes: String,
    val streamUrl: String,
    val country: String,
    val homepage: String,
    val codec: String,
    val bitrate: String,
    val favicon: String,
    val tags: List<String>,
)
