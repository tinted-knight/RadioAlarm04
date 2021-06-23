package com.noomit.domain.alarm_manager

interface AlarmSchedulerContract {
    fun schedule(alarmId: Long, bellUrl: String, bellName: String, timeInMillis: Long)
    fun clearAlarms()
}
