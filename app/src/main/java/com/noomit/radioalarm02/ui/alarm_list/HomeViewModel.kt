package com.noomit.radioalarm02.ui.alarm_list

import androidx.hilt.lifecycle.ViewModelInject
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.domain.alarm_manager.AlarmManagerContract
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmAdapterActions
import com.noomit.radioalarm02.ui.navigation.NavCommand
import com.noomit.radioalarm02.ui.navigation.NavigationViewModel

sealed class AlarmListDirections : NavCommand {
    object Favorites : AlarmListDirections()
    object AddAlarm : AlarmListDirections()
    object RadioBrowser : AlarmListDirections()
}

// #todo provide here alamManager as service; get rid of AlarmManagerViewModel
class HomeViewModel @ViewModelInject constructor(
    private val alarmManager: AlarmManagerContract,
) : NavigationViewModel<AlarmListDirections>(), IHomeLayoutDelegate, AlarmAdapterActions {

    val alarms = alarmManager.alarms

    fun insert(hour: Int, minute: Int) {
        alarmManager.insert(hour, minute)
    }

    override fun onFavoriteClick() {
        navigateTo(AlarmListDirections.Favorites)
    }

    override fun onAddAlarmClick() {
        navigateTo(AlarmListDirections.AddAlarm)
    }

    override fun onBrowseClick() {
        navigateTo(AlarmListDirections.RadioBrowser)
    }

    override fun onDeleteClick(alarm: Alarm) {
        // #todo toast
//        alarmManager.delete(alarm)
    }

    override fun onDeleteLongClick(alarm: Alarm) {
        alarmManager.delete(alarm)
    }

    override fun onEnabledChecked(alarm: Alarm, isChecked: Boolean) {
        alarmManager.setEnabled(alarm, isChecked)
    }

    override fun onTimeClick(alarm: Alarm) {
        // #todo show time dialog
    }

    override fun onMelodyClick(alarm: Alarm) {
        alarmManager.selectMelodyFor(alarm)
        // #todo goto melody select
    }

    override fun onMelodyLongClick(alarm: Alarm) {
        // #todo melody long click
    }

    override fun onDayOfWeekClick(day: Int, alarm: Alarm) {
        alarmManager.updateDayOfWeek(day, alarm)
    }
}
