package com.noomit.radioalarm02.ui.alarm_fire

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.noomit.radioalarm02.domain.alarm_manager.FiredAlarmManagerContract
import com.noomit.radioalarm02.model.clearScheduledAlarms
import com.noomit.radioalarm02.model.reComposeFired
import com.noomit.radioalarm02.model.scheduleAlarm
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

private fun plog(message: String) =
    Timber.tag("tagg-app-dismiss_alarm").i("$message [${Thread.currentThread().name}]")

class DismissAlarmViewModel @ViewModelInject constructor(
    private val manager: FiredAlarmManagerContract,
    @ApplicationContext private val context: Context,
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
        plog("DismissAlarmViewModel::init")
    }

    override fun onCleared() {
        plog("DismissAlarmViewModel::onCleared")
        super.onCleared()
    }

    fun alarmFired() = alarmId?.let {
        val alarm = manager.selectById(it)
        val updated = reComposeFired(alarm)
        val c = Calendar.getInstance().apply {
            timeInMillis = updated.timeInMillis
        }
        plog("updated: ${c[Calendar.DAY_OF_MONTH]}:${c[Calendar.MONTH]}")
        manager.updateTimeInMillis(
            id = updated.id,
            timeInMillis = updated.timeInMillis,
        )
        val nextAlarm = manager.nextActive
        // #think nextAlarm?.also { } ?: clearSchAl()
        if (nextAlarm != null) {
            val cal = Calendar.getInstance().apply { timeInMillis = nextAlarm.timeInMillis }
            plog("next: ${cal[Calendar.DAY_OF_MONTH]}/${cal[Calendar.MONTH]};${cal[Calendar.HOUR_OF_DAY]}:${cal[Calendar.MINUTE]}")
            scheduleAlarm(
                context = context,
                alarmId = nextAlarm.id,
                bellUrl = nextAlarm.bellUrl,
                bellName = nextAlarm.bellName,
                timeInMillis = nextAlarm.timeInMillis,
            )
        } else {
            clearScheduledAlarms(context)
        }
    }
}
