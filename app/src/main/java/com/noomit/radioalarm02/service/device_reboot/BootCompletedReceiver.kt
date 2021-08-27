package com.noomit.radioalarm02.service.device_reboot

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

class BootCompletedReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED) return

        val context = context ?: return
        val workRequest = OneTimeWorkRequestBuilder<ScheduleAlarmWorker>().build()
        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
