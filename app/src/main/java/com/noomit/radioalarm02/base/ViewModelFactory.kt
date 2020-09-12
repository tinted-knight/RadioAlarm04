package com.noomit.radioalarm02.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.favoritesview.FavoritesViewModel
import com.noomit.radioalarm02.radiobrowserview.RadioBrowserViewModel
import com.noomit.radioalarm02.ui.AlarmManagerViewModel

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

@Suppress("UNCHECKED_CAST")
class DatabaseViewModelFactory(private val database: Database) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            FavoritesViewModel::class.java -> FavoritesViewModel(database) as T
            AlarmManagerViewModel::class.java -> AlarmManagerViewModel(database) as T
            else -> throw IllegalArgumentException("Cannot find ViewModel class to create from factory")
        }
    }
}