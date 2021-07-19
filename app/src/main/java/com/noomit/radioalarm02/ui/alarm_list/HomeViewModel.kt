package com.noomit.radioalarm02.ui.alarm_list

import androidx.lifecycle.viewModelScope
import com.noomit.domain.alarm_manager.AlarmManager
import com.noomit.domain.entities.AlarmModel
import com.noomit.domain.entities.StationModel
import com.noomit.domain.server_manager.ServerManager
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmAdapterActions
import com.noomit.radioalarm02.ui.navigation.NavigationViewModel
import com.noomit.radioalarm02.ui.navigation.OneShotEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AlarmListEvent : OneShotEvent {
    object Favorites : AlarmListEvent()
    object AddAlarm : AlarmListEvent()
    object RadioBrowser : AlarmListEvent()
    object HoldToDelete : AlarmListEvent()
    object SelectMelody : AlarmListEvent()
    data class TestMelody(val alarm: AlarmModel) : AlarmListEvent()
    data class TimeChange(val alarm: AlarmModel) : AlarmListEvent()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val manager: AlarmManager,
    private val serverManager: ServerManager,
) : NavigationViewModel<AlarmListEvent>(), IHomeLayoutDelegate, AlarmAdapterActions {

    val alarms = manager.alarms

    init {
        viewModelScope.launch(Dispatchers.IO) {
            serverManager.getAvalilable()
        }
        viewModelScope.launch {
            manager.observeNextActive()
//            serverManager.activeServer.collect {
//                if (it is ActiveServerState.Value) {
//                    ilog(it.serverInfo.urlString)
//                }
//            }
        }
    }

    fun insert(hour: Int, minute: Int) = manager.insert(hour, minute)

    fun updateTime(alarm: AlarmModel, hour: Int, minute: Int) =
        manager.updateTime(alarm, hour, minute)

    fun setMelody(favorite: StationModel) = manager.setMelody(favorite)

    fun setDefaultRingtone() = manager.setDefaultRingtone()

    override fun onFavoriteClick() {
        navigateTo(AlarmListEvent.Favorites)
    }

    override fun onAddAlarmClick() {
        navigateTo(AlarmListEvent.AddAlarm)
    }

    override fun onBrowseClick() {
        navigateTo(AlarmListEvent.RadioBrowser)
    }

    override fun onDeleteClick(alarm: AlarmModel) {
        navigateTo(AlarmListEvent.HoldToDelete)
    }

    override fun onDeleteLongClick(alarm: AlarmModel) {
        manager.delete(alarm)
    }

    override fun onEnabledChecked(alarm: AlarmModel, isChecked: Boolean) {
        manager.setEnabled(alarm, isChecked)
    }

    override fun onTimeClick(alarm: AlarmModel) {
        navigateTo(AlarmListEvent.TimeChange(alarm))
    }

    override fun onMelodyClick(alarm: AlarmModel) {
        manager.selectMelodyFor(alarm)
        navigateTo(AlarmListEvent.SelectMelody)
    }

    override fun onMelodyLongClick(alarm: AlarmModel) {
        navigateTo(AlarmListEvent.TestMelody(alarm))
    }

    override fun onDayOfWeekClick(day: Int, alarm: AlarmModel) {
        manager.updateDayOfWeek(day, alarm)
    }
}
