package com.noomit.radioalarm02.alarm.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import com.noomit.radioalarm02.AlarmReceiver
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.alarm.DismissAlarmViewModel
import com.noomit.radioalarm02.base.AndroidViewModelFactory
import com.noomit.radioalarm02.base.BaseWakelockActivity
import com.noomit.radioalarm02.model.AppDatabase
import timber.log.Timber

private fun plog(message: String) = Timber.tag("tagg-alarm_activity").i(message)

class AlarmActivity : BaseWakelockActivity() {

    private val viewModel: DismissAlarmViewModel by viewModels {
        AndroidViewModelFactory(AppDatabase.getInstance(this), application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        viewModel.alarmId = intent.getLongExtra(AlarmReceiver.ALARM_ID, -1)
        viewModel.melodyUrl = intent.getStringExtra(AlarmReceiver.BELL_URL)

        val action = intent.action ?: ACTION_TEST
        if (action == ACTION_FIRE) {
            findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
                viewModel.alarmFired()
                onBackPressed()
            }
        } else {
            findViewById<Button>(R.id.btn_dismiss).setOnClickListener {
                onBackPressed()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            viewModel.alarmId = it.getLongExtra(AlarmReceiver.ALARM_ID, -1)
            viewModel.melodyUrl = it.getStringExtra(AlarmReceiver.BELL_URL)
        }
    }

    companion object {
        const val ACTION_FIRE = "alarm-action"
        const val ACTION_TEST = "alarm-test"
    }
}