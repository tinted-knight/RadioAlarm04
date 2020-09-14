package com.noomit.radioalarm02.model

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.noomit.radioalarm02.AlarmReceiver
import com.noomit.radioalarm02.MainActivity

fun scheduleAlarm(context: Context, alarmId: Long, bellId: Int, timeInMillis: Long) {
    val operation = composePendingIntent(context, alarmId, bellId)
    cancelAlarm(context, operation)
    setWithAlarmClock(context, timeInMillis, operation)
}

fun clearScheduledAlarms(context: Context) {
    cancelAlarm(context, composePendingIntent(context, -1, 0))
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

private fun composePendingIntent(context: Context, alarmId: Long, bellId: Int) =
    PendingIntent.getBroadcast(
        context,
        101, // #fake
        Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ALARM_ACTION
            putExtra(AlarmReceiver.ALARM_ID, alarmId)
            putExtra(AlarmReceiver.BELL_ID, bellId)
        },
        PendingIntent.FLAG_UPDATE_CURRENT,
    )