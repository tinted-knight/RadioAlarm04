package com.noomit.radioalarm02

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.noomit.radioalarm02.base.AndroidViewModelFactory
import com.noomit.radioalarm02.model.AppDatabase
import timber.log.Timber

private fun plog(message: String) = Timber.tag("tagg-alarm_activity").i(message)

class AlarmActivity : AppCompatActivity() {

    private val viewModel: DismissAlarmViewModel by viewModels {
        AndroidViewModelFactory(AppDatabase.getInstance(this), application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        viewModel.alarmId = intent.getLongExtra(AlarmReceiver.ALARM_ID, -1)
        viewModel.melodyUrl = intent.getStringExtra(AlarmReceiver.BELL_URL)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            viewModel.alarmId = it.getLongExtra(AlarmReceiver.ALARM_ID, -1)
            viewModel.melodyUrl = it.getStringExtra(AlarmReceiver.BELL_URL)
        }
    }
}