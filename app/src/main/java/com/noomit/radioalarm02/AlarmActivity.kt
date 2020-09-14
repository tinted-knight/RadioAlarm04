package com.noomit.radioalarm02

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

private fun plog(message: String) = Timber.tag("tagg-alarm_activity").i(message)

class AlarmActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            plog("alarmId = ${it.getLongExtra(AlarmReceiver.ALARM_ID, -1)}")
            plog("bellId = ${it.getIntExtra(AlarmReceiver.BELL_ID, -1)}")
        }
    }
}