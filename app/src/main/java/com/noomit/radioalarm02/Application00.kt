package com.noomit.radioalarm02

import android.app.Application
import android.content.Context
import android.os.Build
import android.widget.Toast
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class Application00 : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())

//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun tplog(message: String) = Timber.tag("tagg-main").i(message)

inline fun <T> getResourceApi23(more: () -> T, less: () -> T) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        more()
    } else {
        less()
    }
