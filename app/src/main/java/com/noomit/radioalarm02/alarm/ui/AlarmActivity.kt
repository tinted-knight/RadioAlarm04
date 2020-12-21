package com.noomit.radioalarm02.alarm.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.viewModels
import com.noomit.radioalarm02.AlarmReceiver
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.alarm.DismissAlarmViewModel
import com.noomit.radioalarm02.base.AndroidViewModelFactory
import com.noomit.radioalarm02.base.BaseWakelockActivity
import com.noomit.radioalarm02.data.AppDatabase

class AlarmActivity : BaseWakelockActivity() {

    private val viewModel: DismissAlarmViewModel by viewModels {
        AndroidViewModelFactory(AppDatabase.getInstance(this), application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        setWindowTransparency()

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

    private fun setWindowTransparency() {
        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LOW_PROFILE
        }
        window.statusBarColor = Color.TRANSPARENT
        window.navigationBarColor = Color.TRANSPARENT
    }

    companion object {
        const val ACTION_FIRE = "alarm-action"
        const val ACTION_TEST = "alarm-test"
    }
}
