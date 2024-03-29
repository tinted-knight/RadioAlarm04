package com.noomit.radioalarm02.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.ui.alarm_fire.AlarmActivity
import com.noomit.radioalarm02.versionMPlus
import com.noomit.alarmtheme.R as Rtheme

class AlarmReceiver : BroadcastReceiver() {
  @SuppressLint("InlinedApi")
  override fun onReceive(context: Context?, intent: Intent?) {
    if (context == null) return
    if (intent?.action == ALARM_ACTION) {
      val alarmId = intent.getLongExtra(ALARM_ID, -1)
      val melodyUrl = intent.getStringExtra(BELL_URL)
      val melodyName = intent.getStringExtra(BELL_NAME)

      val pendingIntent = PendingIntent.getActivity(
        context,
        REQUEST_CODE,
        AlarmActivity.composeRealIntent(
          context = context,
          id = alarmId,
          url = melodyUrl,
          name = melodyName,
        ),
        PendingIntent.FLAG_UPDATE_CURRENT or if (versionMPlus) PendingIntent.FLAG_IMMUTABLE else 0,
      )
      val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(Rtheme.drawable.ic_radio_24)
        .setContentTitle(context.getString(R.string.notif_i_am))
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setFullScreenIntent(pendingIntent, true)
      val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        manager.createNotificationChannel(
          NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notif_channel_radio_alarm),
            NotificationManager.IMPORTANCE_HIGH,
          )
        )
      }
      manager.notify(NOTIF_ID, builder.build())
    }
  }

  companion object {
    const val ALARM_ACTION = "receiver-alarm-action"
    const val ALARM_ID = "alarm-id"
    const val BELL_URL = "bell-id"
    const val BELL_NAME = "bell-name"
    const val CHANNEL_ID = "channel-id"
    const val NOTIF_ID = 1002
    private const val REQUEST_CODE = 102
  }
}
