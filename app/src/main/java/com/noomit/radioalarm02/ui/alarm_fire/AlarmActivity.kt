package com.noomit.radioalarm02.ui.alarm_fire

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.ncorti.slidetoact.SlideToActView
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.service.AlarmReceiver
import com.noomit.radioalarm02.service.PlayerService
import com.noomit.radioalarm02.util.BaseWakelockActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmActivity : BaseWakelockActivity() {

    private val viewModel: DismissAlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        setWindowDecoration()

        viewModel.alarmId = intent.getLongExtra(AlarmReceiver.ALARM_ID, -1)
        viewModel.melodyUrl = intent.getStringExtra(AlarmReceiver.BELL_URL)
        viewModel.melodyName = intent.getStringExtra(AlarmReceiver.BELL_NAME)

        val action = intent.action ?: ACTION_TEST
        findViewById<SlideToActView>(R.id.slide_to_wake).onSlideCompleteListener = object :
            SlideToActView.OnSlideCompleteListener {
            override fun onSlideComplete(view: SlideToActView) {
                if (action == ACTION_FIRE) {
                    viewModel.alarmFired()
                    application.stopService(PlayerService.intent(this@AlarmActivity))
                    onBackPressed()
                } else {
                    onBackPressed()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            viewModel.alarmId = it.getLongExtra(AlarmReceiver.ALARM_ID, -1)
            viewModel.melodyUrl = it.getStringExtra(AlarmReceiver.BELL_URL)
            viewModel.melodyName = it.getStringExtra(AlarmReceiver.BELL_NAME)
        }
    }

    override fun onResume() {
        super.onResume()

        application.startService(PlayerService.intent(this))
    }

    private fun setWindowDecoration() {
        val nightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        window.decorView.apply {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                version21to23()
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
                version23to26(nightMode)
                return
            }
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                version27to29(nightMode)
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                version30plus(nightMode)
                return
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun version21to23() {
        window.statusBarColor = Color.parseColor("#40000000")
        window.decorView.apply {
            systemUiVisibility = systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
    }

    @Suppress("DEPRECATION")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun version23to26(nightMode: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.decorView.apply {
            systemUiVisibility = systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    @SuppressLint("InlinedApi")
    @Suppress("DEPRECATION")
    private fun version27to29(nightMode: Int) {
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN or
//                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun version30plus(nightMode: Int) {
        window.setDecorFitsSystemWindows(false)
        val insetController = window.insetsController ?: return
        when (nightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                insetController.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                )
                insetController.setSystemBarsAppearance(
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                )
            }
            Configuration.UI_MODE_NIGHT_YES -> {
                insetController.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
                )
                insetController.setSystemBarsAppearance(
                    0,
                    WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
                )
            }
        }
    }

    companion object {
        const val ACTION_FIRE = "alarm-action"
        const val ACTION_TEST = "alarm-test"

        fun composeIntent(
            context: Context,
            id: Long,
            url: String?,
            name: String?,
            action: String,
            flags: Int,
        ) = Intent(context, AlarmActivity::class.java).apply {
            addFlags(flags)
            this.action = action
            putExtra(AlarmReceiver.ALARM_ID, id)
            putExtra(AlarmReceiver.BELL_URL, url)
            putExtra(AlarmReceiver.BELL_NAME, name)
        }

    }
}
