package com.noomit.radioalarm02.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.radiobrowserview.RadioBrowserViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val apiService: RadioBrowserService) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            RadioBrowserViewModel::class.java -> RadioBrowserViewModel(apiService) as T
            else -> throw IllegalArgumentException("Cannot find ViewModel class to create from factory")
        }
    }
}