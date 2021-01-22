package com.noomit.radioalarm02.domain.alarm_manager

import android.content.Context
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.data.StationModel
import com.noomit.radioalarm02.model.*
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class AlarmManager @Inject constructor(
    database: Database,
    @ApplicationContext private val context: Context,
) : AlarmManagerContract {

    private fun plog(message: String) = Timber.tag("tagg-alarm_manager").i(message)

    private val queries = database.alarmQueries

    override val alarms = queries.selectAll().asFlow()
        .flowOn(Dispatchers.IO)
        .mapToList()
        .map { list -> list.sortedBy { it.time_in_millis } }
        .map { list -> list.sortedByDescending { it.is_enabled } }
        .flowOn(Dispatchers.Default)

    override fun insert(hour: Int, minute: Int) {
        val alarm = composeAlarmEntity(hour, minute)
        plog("AlarmManagerViewModel.insert, $alarm")
        queries.insert(
            hour = alarm.hour,
            minute = alarm.minute,
            is_enabled = alarm.isEnabled,
            bell_url = alarm.bellUrl,
            bell_name = alarm.bellName,
            repeat = alarm.repeat,
            days_of_week = alarm.daysOfWeek,
            time_in_millis = alarm.timeInMillis,
        )
    }

    override fun delete(alarm: Alarm) {
        queries.delete(id = alarm.id)
    }

    override fun updateDayOfWeek(dayToSwitch: Int, alarm: Alarm) {
        val updated = reCompose(alarm, dayToSwitch)
        val c = Calendar.getInstance().apply {
            timeInMillis = updated.time_in_millis
        }
        plog("updated: ${c[Calendar.DAY_OF_MONTH]}/${c[Calendar.MONTH]}, daysOfWeek = ${updated.days_of_week}")
        queries.updateDays(
            alarmId = updated.id,
            daysOfWeek = updated.days_of_week,
            timeInMillis = updated.time_in_millis,
            isEnabled = updated.is_enabled,
        )
    }

    override fun updateTime(alarm: Alarm, hour: Int, minute: Int) {
        plog("updateTime to $hour:$minute")
        val updated = reComposeFired(alarm.copy(hour = hour, minute = minute))
        queries.updateTime(
            alarmId = alarm.id,
            hour = updated.hour,
            minute = updated.minute,
            timeInMillis = updated.time_in_millis,
        )
    }

    override fun setEnabled(alarm: Alarm, isEnabled: Boolean) {
        plog("setEnabled, $isEnabled")
        if (!isEnabled) {
            queries.updateEnabled(alarmId = alarm.id, isEnabled = false)
            return
        }

        if (alarm.days_of_week == 0) {
            val composed = composeAlarmEntity(alarm.hour, alarm.minute)
            queries.updateDays(
                alarmId = alarm.id,
                daysOfWeek = composed.daysOfWeek,
                timeInMillis = composed.timeInMillis,
                isEnabled = true,
            )
            return
        }

        if (isEnabled && alarm.days_of_week != 0) {
            val reEnabled = reComposeFired(alarm)
            queries.updateDays(
                daysOfWeek = reEnabled.days_of_week,
                isEnabled = true,
                timeInMillis = reEnabled.time_in_millis,
                alarmId = reEnabled.id,
            )
        }
    }

    private var selectMelodyFor: Alarm? = null

    override fun selectMelodyFor(alarm: Alarm) {
        selectMelodyFor = alarm
    }

    override fun setMelody(favorite: StationModel) {
        selectMelodyFor?.let {
            queries.updateMelody(
                alarmId = it.id,
                melodyUrl = favorite.streamUrl,
                melodyName = favorite.name,
            )
        }
    }

    override fun setDefaultRingtone() {
        selectMelodyFor?.let {
            queries.updateMelody(
                alarmId = it.id,
                melodyUrl = "",
                melodyName = "system",
            )
        }
    }

    override suspend fun observeNextActive() {
        queries.nextActive()
            .asFlow()
            .mapToOneOrNull()
            .onEach {
                if (it != null) {
                    val c = Calendar.getInstance().apply { timeInMillis = it.time_in_millis }
                    plog("next: ${c[Calendar.DAY_OF_MONTH]}/${c[Calendar.MONTH]};${c[Calendar.HOUR_OF_DAY]}:${c[Calendar.MINUTE]}")
                    scheduleAlarm(
                        context = context,
                        alarmId = it.id,
                        bellUrl = it.bell_url,
                        bellName = it.bell_name,
                        timeInMillis = it.time_in_millis,
                    )
                } else {
                    clearScheduledAlarms(context)
                }
            }
            .collect()
    }
}
