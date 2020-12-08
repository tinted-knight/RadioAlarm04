package com.noomit.radioalarm02.ui.alarm_list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.Favorite
import com.noomit.radioalarm02.model.*
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

class AlarmManagerViewModel(database: Database, application: Application) :
    AndroidViewModel(application) {

    private fun plog(message: String) =
        Timber.tag("tagg-app-alarm_manager").i("$message [${Thread.currentThread().name}]")

    private val queries = database.alarmQueries

    init {
        plog("AlarmManagerViewModel::init")
        observeNextActive()
    }

    val alarms = queries.selectAll().asFlow().mapToList().asLiveData()

    fun insert(hour: Int, minute: Int) {
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

    fun delete(alarm: Alarm) = queries.delete(id = alarm.id)

    fun updateDayOfWeek(dayToSwitch: Int, alarm: Alarm) {
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

    fun updateTime(alarm: Alarm, hour: Int, minute: Int) {
        plog("updateTime to $hour:$minute")
        val updated = reComposeFired(alarm.copy(hour = hour, minute = minute))
        queries.updateTime(
            alarmId = alarm.id,
            hour = updated.hour,
            minute = updated.minute,
            timeInMillis = updated.time_in_millis,
        )
    }

    fun setEnabled(alarm: Alarm, isEnabled: Boolean) {
        if (!isEnabled || alarm.days_of_week != 0) {
            queries.updateEnabled(alarmId = alarm.id, isEnabled = isEnabled)
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
        }
    }

    private var selectMelodyFor: Alarm? = null

    fun selectMelodyFor(alarm: Alarm) {
        selectMelodyFor = alarm
    }

    fun setMelody(favorite: Favorite) {
        selectMelodyFor?.let {
            queries.updateMelody(
                alarmId = it.id,
                melodyUrl = favorite.stream_url,
                melodyName = favorite.name,
            )
        }
    }

    private fun observeNextActive() = viewModelScope.launch {
        queries.nextActive()
            .asFlow()
            .mapToOneOrNull()
            .onEach {
                if (it != null) {
                    val c = Calendar.getInstance().apply { timeInMillis = it.time_in_millis }
                    plog("next: ${c[Calendar.DAY_OF_MONTH]}/${c[Calendar.MONTH]};${c[Calendar.HOUR_OF_DAY]}:${c[Calendar.MINUTE]}")
                    scheduleAlarm(
                        context = getApplication(),
                        alarmId = it.id,
                        bellUrl = it.bell_url,
                        timeInMillis = it.time_in_millis,
                    )
                } else {
                    clearScheduledAlarms(getApplication())
                }
            }
            .collect()
    }
}