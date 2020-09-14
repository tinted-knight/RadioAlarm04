package com.noomit.radioalarm02.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.model.clearScheduledAlarms
import com.noomit.radioalarm02.model.composeAlarmEntity
import com.noomit.radioalarm02.model.scheduleAlarm
import com.noomit.radioalarm02.model.switchBitByDay
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber

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
            bell_id = alarm.bellId,
            bell_name = alarm.bellName,
            repeat = alarm.repeat,
            days_of_week = alarm.daysOfWeek,
            time_in_millis = alarm.timeInMillis,
        )
    }

    fun delete(alarm: Alarm) = queries.delete(id = alarm.id)

    fun updateDayOfWeek(dayToSwitch: Int, alarm: Alarm) = queries.updateDays(
        daysOfWeek = switchBitByDay(dayToSwitch, alarm.days_of_week),
        alarmId = alarm.id,
    )

    private fun observeNextActive() = viewModelScope.launch {
        queries.nextActive()
            .asFlow()
            .mapToOneOrNull()
            .onEach {
                if (it != null) {
                    scheduleAlarm(
                        context = getApplication(),
                        alarmId = it.id,
                        bellId = it.bell_id,
                        timeInMillis = it.time_in_millis,
                    )
                } else {
                    clearScheduledAlarms(getApplication())
                }
            }
            .collect()
    }
}