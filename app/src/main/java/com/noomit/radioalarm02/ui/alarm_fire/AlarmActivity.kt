package com.noomit.radioalarm02.ui.alarm_fire

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
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

  private lateinit var serviceIntent: Intent

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_alarm)
    setWindowDecoration()

    serviceIntent = PlayerService.intent(this)

    viewModel.alarmId = intent.getLongExtra(AlarmReceiver.ALARM_ID, -1)
    viewModel.melodyUrl = intent.getStringExtra(AlarmReceiver.BELL_URL)
    viewModel.melodyName = intent.getStringExtra(AlarmReceiver.BELL_NAME)

    val action = intent.action ?: ACTION_TEST
    findViewById<SlideToActView>(R.id.slide_to_wake).onSlideCompleteListener = object :
      SlideToActView.OnSlideCompleteListener {
      override fun onSlideComplete(view: SlideToActView) {
        if (action == ACTION_FIRE) {
          viewModel.alarmFired()
          application.stopService(serviceIntent)
        }
        finish()
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

    application.startService(serviceIntent)
  }

  override fun onBackPressed() {
    if (intent.action == ACTION_FIRE) {
      viewModel.alarmFired()
      application.stopService(serviceIntent)
    }
    // to get rid of LeakCanary notification
    finishAfterTransition()
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
        version30plus()
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
  private fun version30plus() {
    window.setDecorFitsSystemWindows(false)
  }

  companion object {
    const val ACTION_FIRE = "alarm-action"
    const val ACTION_TEST = "alarm-test"

    fun composeTestIntent(context: Context, id: Long, url: String?, name: String?) =
      composeIntent(
        context = context,
        id = id,
        url = url,
        name = name,
        action = ACTION_TEST,
        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
      )

    fun composeRealIntent(context: Context, id: Long, url: String?, name: String?) =
      composeIntent(
        context = context,
        id = id,
        url = url,
        name = name,
        action = ACTION_FIRE,
        flags = 0
      )

    private fun composeIntent(
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
