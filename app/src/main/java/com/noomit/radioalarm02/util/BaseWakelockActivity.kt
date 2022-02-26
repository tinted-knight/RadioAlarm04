package com.noomit.radioalarm02.util

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.noomit.radioalarm02.ilog

@Suppress("DEPRECATION")
abstract class BaseWakelockActivity : AppCompatActivity() {

  companion object {
    const val WAKELOCK_TAG = "radioalarm:wakelog-tag"
  }

  private lateinit var wakeLock: PowerManager.WakeLock

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
      wakeUp27plus()
    } else {
      wakeUp27less()
    }

  }

  override fun onDestroy() {
    super.onDestroy()
    releaseWakelock()
  }

  private fun releaseWakelock() {
    if (::wakeLock.isInitialized && wakeLock.isHeld) wakeLock.release()
  }

  private fun wakeUp27less() {
    acquireWakelock()
    window.addFlags(
      WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
    )
  }

  private fun acquireWakelock() {
    val powerManager = getSystemService(POWER_SERVICE) as PowerManager
    wakeLock = powerManager.newWakeLock(
      PowerManager.SCREEN_DIM_WAKE_LOCK
        or PowerManager.ACQUIRE_CAUSES_WAKEUP, WAKELOCK_TAG
    )
//        wakeLock.acquire()
    wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)
  }

  @RequiresApi(Build.VERSION_CODES.O_MR1)
  private fun wakeUp27plus() {
    setShowWhenLocked(true)
    setTurnScreenOn(true)

    // Keyguard leak: https://stackoverflow.com/questions/60477120/keyguardmanager-memory-leak
    val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
    if (keyguardManager.isKeyguardLocked) {
      keyguardManager.requestDismissKeyguard(this,
        object : KeyguardManager.KeyguardDismissCallback() {
          override fun onDismissSucceeded() {
            super.onDismissSucceeded()
            ilog("onDismissSucceeded")
          }

          override fun onDismissCancelled() {
            super.onDismissCancelled()
            ilog("onDismissCancelled")
          }

          override fun onDismissError() {
            super.onDismissError()
            ilog("onDismissError")
          }
        })
    }
  }
}
