package com.noomit.radioalarm02

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.noomit.radioalarm02.base.AndroidViewModelFactory
import com.noomit.radioalarm02.databinding.ActivityAlarmBinding
import com.noomit.radioalarm02.model.AppDatabase
import timber.log.Timber

private fun plog(message: String) = Timber.tag("tagg-alarm_activity").i(message)

class AlarmActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityAlarmBinding

    private var alarmId: Long? = null

    private val viewModel: DismissAlarmViewModel by viewModels {
        AndroidViewModelFactory(AppDatabase.getInstance(this), application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityAlarmBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        alarmId = intent.getLongExtra(AlarmReceiver.ALARM_ID, -1)
        plog("alarmId = $alarmId")
        plog("bellId = ${intent.getStringExtra(AlarmReceiver.BELL_URL)}")

        viewBinding.btnClose.setOnClickListener {
            plog("btnClose click")
            alarmId?.let { viewModel.alarmFired(it) }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            alarmId = it.getLongExtra(AlarmReceiver.ALARM_ID, -1)
            plog("alarmId = $alarmId")
            plog("bellId = ${it.getStringExtra(AlarmReceiver.BELL_URL)}")
        }
    }
}