package com.noomit.radioalarm02.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

interface OneShotEvent

object NavHelper {
    const val title = "title"
}

// #todo naming and look into Channel.comsumeAsFlow/collectAsFlow
abstract class NavigationViewModel<T : OneShotEvent> : ViewModel() {
    private val navigation = MutableSharedFlow<T>(replay = 0)
    val oneshotEvents: SharedFlow<T>
        get() = navigation

    protected fun navigateTo(destination: T) {
//        tplog("navigateTo, ${navigation.subscriptionCount.value}")
        viewModelScope.launch { navigation.emit(destination) }
    }
}
