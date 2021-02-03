package com.noomit.domain.entities

import com.noomit.domain.Alarm

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

// #todo fields [hour, minute and repeat] look redundant or need refactoring
//  but they are used in some cases
//  maybe val hour/minute = fun(timeInMillis)
data class AlarmModel(
    val id: Long = -1,
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean,
    val bellUrl: String,
    val bellName: String,
    val repeat: Boolean = false,
    val daysOfWeek: Int,
    val timeInMillis: Long,
) {
    constructor(alarm: Alarm) : this(
        id = alarm.id,
        hour = alarm.hour,
        minute = alarm.minute,
        isEnabled = alarm.is_enabled,
        bellUrl = alarm.bell_url,
        bellName = alarm.bell_name,
        daysOfWeek = alarm.days_of_week,
        timeInMillis = alarm.time_in_millis
    )

}
