package com.noomit.radioalarm02.service

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

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) return
        if (intent?.action == ALARM_ACTION) {
            val alarmId = intent.getLongExtra(ALARM_ID, -1)
            val melodyUrl = intent.getStringExtra(BELL_URL)
            val melodyName = intent.getStringExtra(BELL_NAME)

            val pendingIntent = PendingIntent.getActivity(
                context,
                REQUEST_CODE,
                AlarmActivity.composeIntent(
                    context = context,
                    id = alarmId,
                    url = melodyUrl,
                    name = melodyName,
                    action = AlarmActivity.ACTION_FIRE,
                    flags = 0
                ),
                PendingIntent.FLAG_UPDATE_CURRENT,
            )
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_radio_24)
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
        const val ALARM_ACTION = "alarm-action"
        const val ALARM_ID = "alarm-id"
        const val BELL_URL = "bell-id"
        const val BELL_NAME = "bell-name"
        const val CHANNEL_ID = "channel-id"
        const val NOTIF_ID = 1002
        private const val REQUEST_CODE = 102
    }
}
