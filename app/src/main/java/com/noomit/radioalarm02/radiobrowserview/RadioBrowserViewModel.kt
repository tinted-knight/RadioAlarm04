package com.noomit.radioalarm02.radiobrowserview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.radiobrowser.RadioBrowserService
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app").i("$message, ${Thread.currentThread().name}")

class RadioBrowserViewModel(private val apiService: RadioBrowserService) : ViewModel() {

    val availableServers: LiveData<Boolean> = liveData() {
        plog("RadioBrowserViewModel")
        emit(apiService.getAvailableServers())
    }

}
