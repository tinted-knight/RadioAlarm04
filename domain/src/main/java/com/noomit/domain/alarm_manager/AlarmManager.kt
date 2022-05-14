package com.noomit.domain.alarm_manager

import com.noomit.domain.AlarmQueries
import com.noomit.domain.alarm_manager.alarm_composer.AlarmComposer
import com.noomit.domain.entities.AlarmModel
import com.noomit.domain.entities.StationModel
import kotlinx.coroutines.flow.Flow

interface AlarmManager {
  val alarms: Flow<List<AlarmModel>>
  fun insert(hour: Int, minute: Int)
  fun delete(alarm: AlarmModel)
  fun updateDayOfWeek(dayToSwitch: Int, alarm: AlarmModel)
  fun updateTime(alarm: AlarmModel, hour: Int, minute: Int)
  fun setEnabled(alarm: AlarmModel, isEnabled: Boolean)
  fun selectMelodyFor(alarm: AlarmModel)
  fun setMelody(favorite: StationModel)
  fun setDefaultRingtone()
  fun hasSchedulePermission(): Boolean

  /**
   * Observes database and on each change checks next active alarm and modifies schedule
   * or clears schedule if there are no active alarms
   */
  suspend fun observeNextActive()
}

fun AlarmManager(
  queries: AlarmQueries,
  alarmScheduler: AlarmScheduler,
  alarmComposer: AlarmComposer,
): AlarmManager = AlarmManagerImpl(queries, alarmScheduler, alarmComposer)
