package com.noomit.domain.alarm_manager

interface AlarmScheduler {
  fun schedule(alarmId: Long, bellUrl: String, bellName: String, timeInMillis: Long)
  fun clearAlarms()
  fun hasSchedulePermisson(): Boolean
}
