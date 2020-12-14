package com.noomit.radioalarm02.base

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.noomit.radioalarm02.Application00
import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.alarm.DismissAlarmViewModel
import com.noomit.radioalarm02.ui.alarm_list.AlarmManagerViewModel
import com.noomit.radioalarm02.ui.favorites.FavoritesViewModel
import com.noomit.radioalarm02.ui.radio_browser.RadioBrowserViewModel
import com.noomit.radioalarm02.ui.radio_browser.stationlist.StationViewModel
import kotlinx.coroutines.FlowPreview

@FlowPreview
@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val application: Application00) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            RadioBrowserViewModel::class.java -> {
                application.serviceProvider.run {
                    RadioBrowserViewModel(
                        serverManager,
                        categoryManager,
                        stationManager
                    ) as T
                }
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
class FavoritesViewModelFactory(private val application: Application00) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            StationViewModel::class.java -> StationViewModel(application.serviceProvider.favoritesManager) as T
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
