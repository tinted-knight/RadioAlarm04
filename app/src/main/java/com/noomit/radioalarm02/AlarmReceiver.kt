package com.noomit.radioalarm02

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.noomit.radioalarm02.alarm.ui.AlarmActivity
import timber.log.Timber

private fun plog(message: String) = Timber.tag("tagg-broadcast_receiver").i(message)

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        plog("onReceive")
        if (intent?.action == ALARM_ACTION) {
            val alarmId = intent.getLongExtra(ALARM_ID, -1)
            val melodyUrl = intent.getStringExtra(BELL_URL)
            plog("alarmId = $alarmId")
            plog("bellId = $melodyUrl")
            context?.run {
                val pendingIntent = PendingIntent.getActivity(
                    context,
                    102, // #fake
                    Intent(context, AlarmActivity::class.java).apply {
                        action = ALARM_ACTION
                        putExtra(ALARM_ID, alarmId)
                        putExtra(BELL_URL, melodyUrl)
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT,
                )
                val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_radio_24)
                    .setContentTitle("I am alarm notification")
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setFullScreenIntent(pendingIntent, true)
                val manager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    manager.createNotificationChannel(NotificationChannel(
                        CHANNEL_ID,
                        "radio-alarm", // #fake
                        NotificationManager.IMPORTANCE_HIGH,
                    ))
                }
                manager.notify(NOTIF_ID, builder.build())
            }
        }
    }

    companion object {
        const val ALARM_ACTION = "alarm-action"
        const val ALARM_ID = "alarm-id"
        const val BELL_URL = "bell-id"
        const val CHANNEL_ID = "channel-id"
        const val NOTIF_ID = 1002
    }
}