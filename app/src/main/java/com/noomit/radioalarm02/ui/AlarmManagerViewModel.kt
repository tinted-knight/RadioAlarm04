package com.noomit.radioalarm02.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.Favorite
import com.noomit.radioalarm02.model.clearScheduledAlarms
import com.noomit.radioalarm02.model.composeAlarmEntity
import com.noomit.radioalarm02.model.reCompose
import com.noomit.radioalarm02.model.scheduleAlarm
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOneOrNull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

sealed class SetMelodyState {
    object Idle : SetMelodyState()
    class ForAlarm(val alarm: Alarm) : SetMelodyState()
    class Selected(val alarm: Alarm, val melody: Favorite) : SetMelodyState()
}

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
        plog("${c[Calendar.DAY_OF_MONTH]} : ${c[Calendar.MONTH]}")
        queries.updateDays(
            alarmId = updated.id,
            daysOfWeek = updated.days_of_week,
            timeInMillis = updated.time_in_millis,
            isEnabled = updated.is_enabled,
        )
    }

    fun setEnabled(alarm: Alarm, isEnabled: Boolean) = queries.updateEnabled(isEnabled, alarm.id)

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
                    plog("next: ${c[Calendar.DAY_OF_MONTH]} : ${c[Calendar.MONTH]}")
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