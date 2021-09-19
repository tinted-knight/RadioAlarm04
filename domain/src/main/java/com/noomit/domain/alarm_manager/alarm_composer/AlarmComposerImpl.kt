package com.noomit.domain.alarm_manager.alarm_composer

import com.noomit.domain.alarm_manager.*
import com.noomit.domain.entities.AlarmModel
import java.util.*
import javax.inject.Inject

class AlarmComposerImpl @Inject constructor() : AlarmComposer {

    override fun composeDbEntity(hour: Int, minute: Int): AlarmModel {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            // Cannot fire alarm today because the moment has gone
            // and taking one minute for safety
            val oneMinuteInFuture = Calendar.getInstance().apply { add(Calendar.MINUTE, 1) }
            if (timeInMillis < oneMinuteInFuture.timeInMillis) add(Calendar.DAY_OF_YEAR, 1)
        }

        return AlarmModel(
            hour = calendar.hour,
            minute = calendar.minute,
            isEnabled = true,
            bellUrl = "",
            bellName = "system", // #todo i18n
            repeat = false,
            daysOfWeek = calendar.packWeekInBits(),
            timeInMillis = calendar.timeInMillis,
        )
    }

    override fun reCompose(alarm: AlarmModel, dayOfWeek: Int): AlarmModel {
        val newDays = alarm.switchDayBit(dayOfWeek)
        if (newDays == 0) return alarm.copy(
            daysOfWeek = 0,
            timeInMillis = 0,
            isEnabled = false,
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)

            val oneMinuteInFuture = Calendar.getInstance().apply {
                add(Calendar.MINUTE, 1)
            }
            if (timeInMillis < oneMinuteInFuture.timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
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
            daysOfWeek = newDays,
            timeInMillis = calendar.timeInMillis,
            isEnabled = true,
        )
    }

    override fun reComposeFired(alarm: AlarmModel): AlarmModel {
        val now = Calendar.getInstance()
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)

            if (timeInMillis < now.timeInMillis && alarm.daysOfWeek == get(Calendar.DAY_OF_WEEK)) {
                add(Calendar.WEEK_OF_YEAR, 1)
            }

            val oneMinuteInFuture = now.apply { add(Calendar.MINUTE, 1) }
            if (timeInMillis < oneMinuteInFuture.timeInMillis) {
                add(Calendar.DAY_OF_YEAR, 1)
            }

            var today = get(Calendar.DAY_OF_WEEK)
            repeat(7) {
                if (alarm.daysOfWeek.isDayBitOn(today)) {
                    return@repeat
                } else {
                    add(Calendar.DAY_OF_YEAR, 1)
                    today = get(Calendar.DAY_OF_WEEK)
                }
            }
        }
        return alarm.copy(
            timeInMillis = calendar.timeInMillis,
            isEnabled = true,
        )
    }

    private val Calendar.hour: Int
        get() = get(Calendar.HOUR_OF_DAY)

    private val Calendar.minute: Int
        get() = get(Calendar.MINUTE)

    private val Calendar.dayOfWeek: Int
        get() = get(Calendar.DAY_OF_WEEK)
}
