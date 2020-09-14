package com.noomit.radioalarm02.model

import java.util.*

// #think temporary
data class Alarma(
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean,
    val bellId: Int,
    val repeat: Boolean,
    val daysOfWeek: Int,
    val timeInMillis: Long,
)

fun composeAlarmEntity(hour: Int, minute: Int): Alarma {
    val calendar = Calendar.getInstance().apply {
        val now = timeInMillis
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        // Cannot fire alarm today because the moment has gone
        if (timeInMillis < now) add(Calendar.DAY_OF_YEAR, 1)
    }

    return Alarma(
        hour = calendar.hour,
        minute = calendar.minute,
        isEnabled = true,
        bellId = -1,
        repeat = false,
        daysOfWeek = zipDaysInBits(listOf(calendar.dayOfWeek)),
        timeInMillis = calendar.timeInMillis,
    )
}

private val Calendar.hour: Int
    get() = get(Calendar.HOUR_OF_DAY)

private val Calendar.minute: Int
    get() = get(Calendar.MINUTE)

private val Calendar.dayOfWeek: Int
    get() = get(Calendar.DAY_OF_WEEK)