package com.noomit.radioalarm02

import android.app.Application
import timber.log.Timber

class Application00 : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}