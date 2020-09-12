package com.noomit.radioalarm02.model

import com.noomit.radioalarm02.Alarm
import java.util.*

fun composeAlarmEntity(hour: Int, minute: Int): Alarm {
    val calendar = Calendar.getInstance().apply {
        val now = timeInMillis
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        // Cannot fire alarm today because the moment has gone
        if (timeInMillis < now) add(Calendar.DAY_OF_YEAR, 1)
    }

    return Alarm(
        hour = calendar.hour,
        minute = calendar.minute,
        is_enabled = 1,
        bell_id = -1,
        repeat = 0,
        days_of_week = zipDaysInBits(listOf(calendar.dayOfWeek)).toLong(),
        time_in_millis = calendar.timeInMillis,
    )
}

private val Calendar.hour: Long
    get() = get(Calendar.HOUR_OF_DAY).toLong()

private val Calendar.minute: Long
    get() = get(Calendar.MINUTE).toLong()

private val Calendar.dayOfWeek: Int
    get() = get(Calendar.DAY_OF_WEEK)