package com.noomit.radioalarm02.ui.alarm_list

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import com.noomit.radioalarm02.data.AlarmModel
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
    data class TestMelody(val alarm: AlarmModel) : AlarmListDirections()
    data class TimeChange(val alarm: AlarmModel) : AlarmListDirections()
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

    fun updateTime(alarm: AlarmModel, hour: Int, minute: Int) =
        manager.updateTime(alarm, hour, minute)

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

    override fun onDeleteClick(alarm: AlarmModel) {
        navigateTo(AlarmListDirections.HoldToDelete)
    }

    override fun onDeleteLongClick(alarm: AlarmModel) {
        manager.delete(alarm)
    }

    override fun onEnabledChecked(alarm: AlarmModel, isChecked: Boolean) {
        manager.setEnabled(alarm, isChecked)
    }

    override fun onTimeClick(alarm: AlarmModel) {
        navigateTo(AlarmListDirections.TimeChange(alarm))
    }

    override fun onMelodyClick(alarm: AlarmModel) {
        manager.selectMelodyFor(alarm)
        navigateTo(AlarmListDirections.SelectMelody)
    }

    override fun onMelodyLongClick(alarm: AlarmModel) {
        navigateTo(AlarmListDirections.TestMelody(alarm))
    }

    override fun onDayOfWeekClick(day: Int, alarm: AlarmModel) {
        manager.updateDayOfWeek(day, alarm)
    }
}
