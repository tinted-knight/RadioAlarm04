package com.noomit.radioalarm02.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.model.composeAlarmEntity
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import timber.log.Timber

class AlarmManagerViewModel(database: Database) : ViewModel() {

    private fun plog(message: String) =
        Timber.tag("tagg-app-alarm_manager").i("$message [${Thread.currentThread().name}]")

    private val queries = database.alarmQueries

    init {
        plog("AlarmManagerViewModel::init")
    }

    val alarms = queries.selectAll().asFlow().mapToList().asLiveData()

    fun insert(hour: Int, minute: Int) {
        val alarm = composeAlarmEntity(hour, minute)
        plog("AlarmManagerViewModel.insert, $alarm")
        queries.insert(alarm)
    }
}