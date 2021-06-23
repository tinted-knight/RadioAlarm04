package com.noomit.domain.alarm_manager

import com.noomit.domain.AlarmQueries
import com.noomit.domain.alarm_manager.alarm_composer.AlarmComposer
import com.noomit.domain.entities.AlarmModel
import com.noomit.domain.entities.StationModel
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

internal class AlarmManagerImpl(
    private val queries: AlarmQueries,
    private val alarmScheduler: AlarmSchedulerContract,
    private val alarmComposer: AlarmComposer,
) : AlarmManager {

    override val alarms = queries.selectAll().asFlow()
        .flowOn(Dispatchers.IO)
        .mapToList()
        .map { dbEntities -> dbEntities.map { AlarmModel(it) } }
        .map { list -> list.sortedBy { it.timeInMillis } }
        .map { list -> list.sortedByDescending { it.isEnabled } }
        .flowOn(Dispatchers.Default)

    override fun insert(hour: Int, minute: Int) {
        val alarm = alarmComposer.composeDbEntity(hour, minute)
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

    override fun delete(alarm: AlarmModel) {
        queries.delete(id = alarm.id)
    }

    override fun updateDayOfWeek(dayToSwitch: Int, alarm: AlarmModel) {
        val updated = alarmComposer.reCompose(alarm, dayToSwitch)
        queries.updateDays(
            alarmId = updated.id,
            daysOfWeek = updated.daysOfWeek,
            timeInMillis = updated.timeInMillis,
            isEnabled = updated.isEnabled,
        )
    }

    override fun updateTime(alarm: AlarmModel, hour: Int, minute: Int) {
        val updated = alarmComposer.reComposeFired(alarm.copy(hour = hour, minute = minute))
        queries.updateTime(
            alarmId = alarm.id,
            hour = updated.hour,
            minute = updated.minute,
            timeInMillis = updated.timeInMillis,
        )
    }

    override fun setEnabled(alarm: AlarmModel, isEnabled: Boolean) {
        if (!isEnabled) {
            queries.updateEnabled(alarmId = alarm.id, isEnabled = false)
            return
        }

        if (alarm.daysOfWeek == 0) {
            val composed = alarmComposer.composeDbEntity(alarm.hour, alarm.minute)
            queries.updateDays(
                alarmId = alarm.id,
                daysOfWeek = composed.daysOfWeek,
                timeInMillis = composed.timeInMillis,
                isEnabled = true,
            )
            return
        }

        if (isEnabled && alarm.daysOfWeek != 0) {
            val reEnabled = alarmComposer.reComposeFired(alarm)
            queries.updateDays(
                daysOfWeek = reEnabled.daysOfWeek,
                isEnabled = true,
                timeInMillis = reEnabled.timeInMillis,
                alarmId = reEnabled.id,
            )
        }
    }

    // #achtung Shame
    private var selectMelodyFor: AlarmModel? = null

    override fun selectMelodyFor(alarm: AlarmModel) {
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
                    alarmScheduler.schedule(
                        alarmId = it.id,
                        bellUrl = it.bell_url,
                        bellName = it.bell_name,
                        timeInMillis = it.time_in_millis,
                    )
                } else {
                    alarmScheduler.clearAlarms()
                }
            }
            .collect()
    }

    override fun selectById(id: Long) = AlarmModel(queries.selectById(id).executeAsOne())

    override fun updateTimeInMillis(id: Long, timeInMillis: Long) {
        queries.updateTimeInMillis(
            alarmId = id,
            timeInMillis = timeInMillis,
        )
    }

    override val nextActive: AlarmModel?
        get() {
            val next = queries.nextActive().executeAsOneOrNull()
                ?: return null
            return AlarmModel(next)
        }
}
