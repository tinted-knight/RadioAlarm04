package com.noomit.radioalarm02.base

import com.noomit.domain.Alarm

// #todo fields [hour, minute and repeat] look redundant or need refactoring
//  but they are used in some cases
//  maybe val hour/minute = fun(timeInMillis)

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
