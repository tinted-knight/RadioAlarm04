package com.noomit.radioalarm02.util

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.noomit.domain.alarm_manager.AlarmScheduler
import com.noomit.radioalarm02.MainActivity
import com.noomit.radioalarm02.service.AlarmReceiver
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AlarmSchedulerImpl @Inject constructor(
  @ApplicationContext private val context: Context
) : AlarmScheduler {
  override fun schedule(alarmId: Long, bellUrl: String, bellName: String, timeInMillis: Long) {
    val operation = composePendingIntent(context, alarmId, bellUrl, bellName)
    cancelAlarm(context, operation)
    setWithAlarmClock(context, timeInMillis, operation)
  }

  override fun clearAlarms() {
    cancelAlarm(context, composePendingIntent(context, -1, "", ""))
  }

  @SuppressLint("InlinedApi")
  private fun setWithAlarmClock(context: Context, timeInMillis: Long, operation: PendingIntent) {
    val clockInfo = AlarmManager.AlarmClockInfo(
      timeInMillis,
      // # todo activity to set up alarm
      PendingIntent.getActivity(
        context,
        1002,
        Intent(context, MainActivity::class.java),
        pintentFlag,
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
      pintentFlag,
    )

  private val pintentFlag = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
  } else {
    PendingIntent.FLAG_UPDATE_CURRENT
  }
}
