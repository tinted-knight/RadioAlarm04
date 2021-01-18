package com.noomit.radioalarm02.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noomit.radioalarm02.tplog
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

interface NavCommand

abstract class NavigationViewModel<T : NavCommand> : ViewModel() {
    private val navigation = MutableSharedFlow<T>(replay = 0)
    val commands: SharedFlow<T>
        get() = navigation

    protected fun navigateTo(destination: T) {
//        navigation.value = destination
        tplog("navigateTo, ${navigation.subscriptionCount.value}")
        viewModelScope.launch { navigation.emit(destination) }
    }
}
