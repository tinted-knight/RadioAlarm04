package com.noomit.radioalarm02.domain.alarm_manager

import com.noomit.radioalarm02.data.AlarmModel
import com.noomit.radioalarm02.data.StationModel
import kotlinx.coroutines.flow.Flow

interface AlarmManagerContract {
    val alarms: Flow<List<AlarmModel>>
    fun insert(hour: Int, minute: Int)
    fun delete(alarm: AlarmModel)
    fun updateDayOfWeek(dayToSwitch: Int, alarm: AlarmModel)
    fun updateTime(alarm: AlarmModel, hour: Int, minute: Int)
    fun setEnabled(alarm: AlarmModel, isEnabled: Boolean)
    fun selectMelodyFor(alarm: AlarmModel)
    fun setMelody(favorite: StationModel)
    fun setDefaultRingtone()
    suspend fun observeNextActive()
}
