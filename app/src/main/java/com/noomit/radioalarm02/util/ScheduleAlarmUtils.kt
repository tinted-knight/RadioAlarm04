package com.noomit.radioalarm02.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.noomit.domain.alarm_manager.ScheduleAlarmUtilsContract
import com.noomit.radioalarm02.AlarmReceiver
import com.noomit.radioalarm02.MainActivity

class ScheduleAlarmUtils(private val context: Context) : ScheduleAlarmUtilsContract {
    override fun schedule(alarmId: Long, bellUrl: String, bellName: String, timeInMillis: Long) {
        val operation = composePendingIntent(context, alarmId, bellUrl, bellName)
        cancelAlarm(context, operation)
        setWithAlarmClock(context, timeInMillis, operation)
    }

    override fun clearAlarms() {
        cancelAlarm(context, composePendingIntent(context, -1, "", ""))
    }
}

fun scheduleAlarm(context: Context, alarmId: Long, bellUrl: String, bellName: String, timeInMillis: Long) {
    val operation = composePendingIntent(context, alarmId, bellUrl, bellName)
    cancelAlarm(context, operation)
    setWithAlarmClock(context, timeInMillis, operation)
}

fun clearScheduledAlarms(context: Context) {
    cancelAlarm(context, composePendingIntent(context, -1, "", ""))
}

private fun setWithAlarmClock(context: Context, timeInMillis: Long, operation: PendingIntent) {
    val clockInfo = AlarmManager.AlarmClockInfo(
        timeInMillis,
        // # todo activity to set up alarm
        PendingIntent.getActivity(
            context,
            1002,
            Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT,
        ),
    )
    val systemAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    systemAlarmManager.setAlarmClock(clockInfo, operation)
}

private fun cancelAlarm(context: Context, operation: PendingIntent) {
    val systemAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    systemAlarmManager.cancel(operation)
}

private fun composePendingIntent(context: Context, alarmId: Long, bellUrl: String, bellName: String) =
    PendingIntent.getBroadcast(
        context,
        101, // #fake
        Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ALARM_ACTION
            putExtra(AlarmReceiver.ALARM_ID, alarmId)
            putExtra(AlarmReceiver.BELL_URL, bellUrl)
            putExtra(AlarmReceiver.BELL_NAME, bellName)
        },
        PendingIntent.FLAG_UPDATE_CURRENT,
    )
