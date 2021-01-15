package com.noomit.radioalarm02.base

import android.app.KeyguardManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import timber.log.Timber

private fun plog(message: String) = Timber.tag("tagg-app-wakelock").i(message)

abstract class BaseWakelockActivity : AppCompatActivity() {

    private lateinit var wakeLock: PowerManager.WakeLock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            plog("wakeup version >= 27")
            wakeUp27plus()

        } else {
            plog("wakeup version < 27")
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
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK
                or PowerManager.ACQUIRE_CAUSES_WAKEUP, WAKELOCK_TAG)
//        wakeLock.acquire()
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/)
    }

    @RequiresApi(Build.VERSION_CODES.O_MR1)
    private fun wakeUp27plus() {
        setShowWhenLocked(true)
        setTurnScreenOn(true)

        val keyguardManager = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this,
            object : KeyguardManager.KeyguardDismissCallback() {
                override fun onDismissSucceeded() {
                    super.onDismissSucceeded()
                    plog("onDismissSucceeded")
                }

                override fun onDismissCancelled() {
                    super.onDismissCancelled()
                    plog("onDismissCancelled")
                }

                override fun onDismissError() {
                    super.onDismissError()
                    plog("onDismissError")
                }
            })
    }

    companion object {
        const val WAKELOCK_TAG = "radioalarm:wakelog-tag"
    }
}
