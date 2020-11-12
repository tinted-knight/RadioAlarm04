package com.noomit.radioalarm02.base

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.alarm.DismissAlarmViewModel
import com.noomit.radioalarm02.favoritesview.FavoritesViewModel
import com.noomit.radioalarm02.home.AlarmManagerViewModel
import com.noomit.radioalarm02.radiobrowserview.viewmodels.RadioBrowserViewModel
import com.noomit.radioalarm02.radiobrowserview.viewmodels.ServerManager
import com.noomit.radioalarm02.radiobrowserview.viewmodels.categories.LanguageManager
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val apiService: RadioBrowserService) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            RadioBrowserViewModel::class.java -> {
                val serverManager = ServerManager(apiService)
                val languageManager = LanguageManager(apiService)
                RadioBrowserViewModel(apiService, serverManager, languageManager) as T
            }
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
            else -> throw IllegalArgumentException("Cannot find ViewModel class to create from factory")
        }
    }
}

@Suppress("UNCHECKED_CAST")
class AndroidViewModelFactory(
    private val database: Database,
    private val application: Application,
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            AlarmManagerViewModel::class.java -> AlarmManagerViewModel(database, application) as T
            DismissAlarmViewModel::class.java -> DismissAlarmViewModel(database, application) as T
            else -> throw IllegalArgumentException("Cannot find ViewModel class to create from factory")
        }
    }
}
