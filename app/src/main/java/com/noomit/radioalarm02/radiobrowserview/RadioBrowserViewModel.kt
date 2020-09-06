package com.noomit.radioalarm02.radiobrowserview

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.radiobrowser.RadioBrowserService
import timber.log.Timber

private fun plog(message: String) = Timber.tag("app").d(message)

class RadioBrowserViewModel(private val apiService: RadioBrowserService) : ViewModel() {

    val availableServers: LiveData<Boolean> = liveData() {
        emit(apiService.getAvailableServers())
    }

}
