package com.noomit.domain

sealed class CategoryModel {
    abstract val name: String
    abstract val stationCount: String

    data class Language(
        override val name: String,
        override val stationCount: String,
    ) : CategoryModel()

    data class Tag(
        override val name: String,
        override val stationCount: String,
    ) : CategoryModel()

    data class TopVoted(
        override val name: String = "Top voted",
        override val stationCount: String = "-1",
    ) : CategoryModel()

    data class GlobalSearch(
        override val name: String = "Global search",
        override val stationCount: String = "-1",
        val searchName: String,
        val searchTag: String,
    ) : CategoryModel()
}

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
