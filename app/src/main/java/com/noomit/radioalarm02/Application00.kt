package com.noomit.radioalarm02

import android.app.Application
import android.content.Context
import android.widget.Toast
import timber.log.Timber

class Application00 : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()