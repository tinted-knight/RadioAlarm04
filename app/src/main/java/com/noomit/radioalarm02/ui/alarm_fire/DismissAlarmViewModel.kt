package com.noomit.radioalarm02.ui.alarm_fire

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.noomit.domain.alarm_manager.AlarmManagerContract
import com.noomit.domain.alarm_manager.reComposeFired
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DismissAlarmViewModel @Inject constructor(
    private val manager: AlarmManagerContract,
) : ViewModel() {

    companion object {
        private const val TIMER_TICK_DELAY = 1_000L * 30
    }

    var alarmId: Long? = null
    var melodyUrl: String? = null
    var melodyName: String? = null

    val time = flow<String> {
        val df = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)
        while (true) {
            val now = Calendar.getInstance()
            emit(df.format(now.time))
            delay(TIMER_TICK_DELAY)
        }
    }

    val day: String
        get() {
            val df = SimpleDateFormat("EEEE", Locale.getDefault())
            val now = Date(Calendar.getInstance().timeInMillis)
            return df.format(now)
        }

    init {
        viewModelScope.launch {
            manager.observeNextActive()
        }
    }

    fun alarmFired() = alarmId?.let {
        val alarm = manager.selectById(it)
        val updated = reComposeFired(alarm)
        manager.updateTimeInMillis(
            id = updated.id,
            timeInMillis = updated.timeInMillis,
        )
    }
}
