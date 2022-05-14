package com.noomit.radioalarm02.service.device_reboot

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

@Suppress("NAME_SHADOWING")
class BootCompletedReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context?, intent: Intent?) {
    val action = intent?.action ?: return
    when (action) {
      Intent.ACTION_BOOT_COMPLETED, AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED -> {
        val context = context ?: return
        val workRequest = OneTimeWorkRequestBuilder<ScheduleAlarmWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
      }
    }

  }
}
