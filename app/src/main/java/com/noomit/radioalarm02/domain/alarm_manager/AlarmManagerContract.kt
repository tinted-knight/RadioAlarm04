package com.noomit.radioalarm02.domain.alarm_manager

import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.data.StationModel
import kotlinx.coroutines.flow.Flow

interface AlarmManagerContract {
    val alarms: Flow<List<Alarm>>
    fun insert(hour: Int, minute: Int)
    fun delete(alarm: Alarm)
    fun updateDayOfWeek(dayToSwitch: Int, alarm: Alarm)
    fun updateTime(alarm: Alarm, hour: Int, minute: Int)
    fun setEnabled(alarm: Alarm, isEnabled: Boolean)
    fun selectMelodyFor(alarm: Alarm)
    fun setMelody(favorite: StationModel)
    fun setDefaultRingtone()
    suspend fun observeNextActive()
}
