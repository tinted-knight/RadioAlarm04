package com.noomit.radioalarm02.ui.navigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

interface NavCommand

abstract class NavigationViewModel<T : NavCommand> : ViewModel() {
    private val navigation = SingleLiveEvent<T>()
    val commands = navigation as LiveData<T>

    protected fun navigateTo(destination: T) {
        navigation.value = destination
    }
}
