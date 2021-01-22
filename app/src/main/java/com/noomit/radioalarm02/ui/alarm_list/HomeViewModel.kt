package com.noomit.radioalarm02.ui.alarm_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.data.StationModel
import com.noomit.radioalarm02.domain.alarm_manager.AlarmManagerContract
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmAdapterActions
import com.noomit.radioalarm02.ui.navigation.NavCommand
import com.noomit.radioalarm02.ui.navigation.NavigationViewModel
import kotlinx.coroutines.launch

sealed class AlarmListDirections : NavCommand {
    object Favorites : AlarmListDirections()
    object AddAlarm : AlarmListDirections()
    object RadioBrowser : AlarmListDirections()
    object HoldToDelete : AlarmListDirections()
    object SelectMelody : AlarmListDirections()
    data class TestMelody(val alarm: Alarm) : AlarmListDirections()
    data class TimeChange(val alarm: Alarm) : AlarmListDirections()
}

class HomeViewModel @ViewModelInject constructor(
    private val manager: AlarmManagerContract,
) : NavigationViewModel<AlarmListDirections>(), IHomeLayoutDelegate, AlarmAdapterActions {

    val alarms = manager.alarms

    init {
        viewModelScope.launch {
            manager.observeNextActive()
        }
    }

    fun insert(hour: Int, minute: Int) = manager.insert(hour, minute)

    fun updateTime(alarm: Alarm, hour: Int, minute: Int) = manager.updateTime(alarm, hour, minute)

    fun setMelody(favorite: StationModel) = manager.setMelody(favorite)

    fun setDefaultRingtone() = manager.setDefaultRingtone()

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
        navigateTo(AlarmListDirections.HoldToDelete)
    }

    override fun onDeleteLongClick(alarm: Alarm) {
        manager.delete(alarm)
    }

    override fun onEnabledChecked(alarm: Alarm, isChecked: Boolean) {
        manager.setEnabled(alarm, isChecked)
    }

    override fun onTimeClick(alarm: Alarm) {
        navigateTo(AlarmListDirections.TimeChange(alarm))
    }

    override fun onMelodyClick(alarm: Alarm) {
        manager.selectMelodyFor(alarm)
        navigateTo(AlarmListDirections.SelectMelody)
    }

    override fun onMelodyLongClick(alarm: Alarm) {
        navigateTo(AlarmListDirections.TestMelody(alarm))
    }

    override fun onDayOfWeekClick(day: Int, alarm: Alarm) {
        manager.updateDayOfWeek(day, alarm)
    }
}
