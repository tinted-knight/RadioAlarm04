package com.noomit.radioalarm02

import android.app.Application
import android.content.Context
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

fun Context.toast(message: String?) {
    Toast.makeText(this, message ?: "message is null", Toast.LENGTH_SHORT).show()
}

fun ilog(message: String) = Timber.tag("tagg-main").i(message)
