package com.noomit.radioalarm02.ui.alarm_list

import com.noomit.radioalarm02.ui.navigation.NavCommand
import com.noomit.radioalarm02.ui.navigation.NavigationViewModel

sealed class AlarmListDirections : NavCommand {
    object Favorites : AlarmListDirections()
    object AddAlarm : AlarmListDirections()
    object RadioBrowser : AlarmListDirections()
}

// #todo provide here alamManager as service; get rid of AlarmManagerViewModel
class HomeViewModel : NavigationViewModel<AlarmListDirections>(), IHomeLayoutDelegate {
    override fun onFavoriteClick() {
        navigateTo(AlarmListDirections.Favorites)
    }

    override fun onAddAlarmClick() {
        navigateTo(AlarmListDirections.AddAlarm)
    }

    override fun onBrowseClick() {
        navigateTo(AlarmListDirections.RadioBrowser)
    }
}