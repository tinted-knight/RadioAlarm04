package com.noomit.radioalarm02

import android.app.Application
import android.content.Context
import android.view.View
import android.widget.Toast
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.domain.IServiceProvider
import com.noomit.radioalarm02.domain.ServiceProvider
import timber.log.Timber

class Application00 : Application() {

    private val apiService = RadioBrowserService()
    val serviceProvider: IServiceProvider by lazy(LazyThreadSafetyMode.NONE) {
        ServiceProvider(apiService)
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun View.viewShow() {
    this.visibility = View.VISIBLE
}

fun View.viewHide() {
    this.visibility = View.INVISIBLE
}
