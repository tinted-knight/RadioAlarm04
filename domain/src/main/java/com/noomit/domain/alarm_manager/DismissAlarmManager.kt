package com.noomit.domain.alarm_manager

import com.noomit.domain.AlarmQueries
import com.noomit.domain.alarm_manager.alarm_composer.AlarmComposer
import com.noomit.domain.entities.AlarmModel
import javax.inject.Inject

interface DismissAlarmManager {
  fun selectById(id: Long): AlarmModel
  fun updateTimeInMillis(id: Long, timeInMillis: Long)
  fun scheduleNextActive()
}

class DismissAlarmManagerImpl @Inject constructor(
  private val queries: AlarmQueries,
  private val alarmScheduler: AlarmScheduler,
  private val alarmComposer: AlarmComposer,
) : DismissAlarmManager {

  override fun selectById(id: Long) = AlarmModel(queries.selectById(id).executeAsOne())

  override fun updateTimeInMillis(id: Long, timeInMillis: Long) {
    queries.updateTimeInMillis(
      alarmId = id,
      timeInMillis = timeInMillis,
    )
  }

  override fun scheduleNextActive() {
    val alarm = queries.nextActive().executeAsOneOrNull()
    if (alarm == null) {
      alarmScheduler.clearAlarms()
      return
    }
    alarmScheduler.schedule(
      alarmId = alarm.id,
      bellUrl = alarm.bell_url,
      bellName = alarm.bell_name,
      timeInMillis = alarm.time_in_millis,
    )
  }
}
