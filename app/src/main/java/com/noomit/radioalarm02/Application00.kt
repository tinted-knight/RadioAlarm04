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

fun Context.toast(message: String?) {
    Toast.makeText(this, message ?: "message is null", Toast.LENGTH_SHORT).show()
}

val versionSPlus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

val versionMPlus = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun ilog(message: String) = Timber.tag("tagg-main").i(message)
