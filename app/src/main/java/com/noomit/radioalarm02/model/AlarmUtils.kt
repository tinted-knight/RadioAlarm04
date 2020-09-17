package com.noomit.radioalarm02.model

import com.noomit.radioalarm02.Alarm
import java.util.*

// #think temporary
data class Alarma(
    val hour: Int,
    val minute: Int,
    val isEnabled: Boolean,
    val bellUrl: String,
    val bellName: String,
    val repeat: Boolean,
    val daysOfWeek: Int,
    val timeInMillis: Long,
)

fun composeAlarmEntity(hour: Int, minute: Int): Alarma {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        // Cannot fire alarm today because the moment has gone
        // and taking one minute for safety
        val oneMinuteInFuture = Calendar.getInstance().apply { add(Calendar.MINUTE, 1) }
        if (timeInMillis < oneMinuteInFuture.timeInMillis) add(Calendar.DAY_OF_YEAR, 1)
    }

    return Alarma(
        hour = calendar.hour,
        minute = calendar.minute,
        isEnabled = true,
        bellUrl = "",
        bellName = "system", // #todo i18n
        repeat = false,
        daysOfWeek = zipDaysInBits(listOf(calendar.dayOfWeek)),
        timeInMillis = calendar.timeInMillis,
    )
}

/**
 * !!! Probably (definitely) has bugs
 *
 * Looks for the next day when alarm will be fired
 */
fun reCompose(alarm: Alarm, dayOfWeek: Int): Alarm {
    val newDays = switchBitByDay(dayOfWeek, alarm.days_of_week)
    if (newDays == 0) return alarm.copy(
        days_of_week = 0,
        time_in_millis = 0,
        is_enabled = false,
    )

    val now = Calendar.getInstance()
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, alarm.hour)
        set(Calendar.MINUTE, alarm.minute)

//        if (timeInMillis < now.timeInMillis && alarm.days_of_week == get()) add(Calendar.WEEK_OF_YEAR, 1)

        val oneMinuteInFuture = now.apply { add(Calendar.MINUTE, 1) }
        if (timeInMillis < oneMinuteInFuture.timeInMillis) {
            add(Calendar.DAY_OF_YEAR, 1)
//            newDays = switchBitByDay(get(Calendar.DAY_OF_WEEK), newDays)
        }

        var today = get(Calendar.DAY_OF_WEEK)
        repeat(7) {
            if (newDays.isDayBitOn(today)) {
                return@repeat
            } else {
                add(Calendar.DAY_OF_YEAR, 1)
                today = get(Calendar.DAY_OF_WEEK)
            }
        }
    }
    return alarm.copy(
        days_of_week = newDays,
        time_in_millis = calendar.timeInMillis,
        is_enabled = true,
    )
}

fun reComposeFired(alarm: Alarm): Alarm {
    val now = Calendar.getInstance()
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, alarm.hour)
        set(Calendar.MINUTE, alarm.minute)

        if (timeInMillis < now.timeInMillis && alarm.days_of_week == get(Calendar.DAY_OF_WEEK)) {
            add(Calendar.WEEK_OF_YEAR, 1)
        }

        val oneMinuteInFuture = now.apply { add(Calendar.MINUTE, 1) }
        if (timeInMillis < oneMinuteInFuture.timeInMillis) {
            add(Calendar.DAY_OF_YEAR, 1)
        }

        var today = get(Calendar.DAY_OF_WEEK)
        repeat(7) {
            if (alarm.days_of_week.isDayBitOn(today)) {
                return@repeat
            } else {
                add(Calendar.DAY_OF_YEAR, 1)
                today = get(Calendar.DAY_OF_WEEK)
            }
        }
    }
    return alarm.copy(
        time_in_millis = calendar.timeInMillis,
        is_enabled = true,
    )
}

private val Calendar.hour: Int
    get() = get(Calendar.HOUR_OF_DAY)

private val Calendar.minute: Int
    get() = get(Calendar.MINUTE)

private val Calendar.dayOfWeek: Int
    get() = get(Calendar.DAY_OF_WEEK)

val Alarm.hourString: String
    get() = if (hour > 9) hour.toString() else "0$hour"

val Alarm.minuteString: String
    get() = if (minute > 9) minute.toString() else "0$minute"