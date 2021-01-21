package com.noomit.radioalarm02.ui.alarm_fire

import android.content.Context
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.noomit.radioalarm02.Database
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
    database: Database,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    companion object {
        private const val TIMER_TICK_DELAY = 1_000L * 30
    }

    private val queries = database.alarmQueries

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
        val alarm = queries.selectById(it).executeAsOne()
        val updated = reComposeFired(alarm)
        val c = Calendar.getInstance().apply {
            timeInMillis = updated.time_in_millis
        }
        plog("updated: ${c[Calendar.DAY_OF_MONTH]}:${c[Calendar.MONTH]}")
        queries.updateTimeInMillis(
            alarmId = updated.id,
            timeInMillis = updated.time_in_millis,
        )
        val nextAlarm = queries.nextActive().executeAsOneOrNull()
        if (nextAlarm != null) {
            val cal = Calendar.getInstance().apply { timeInMillis = nextAlarm.time_in_millis }
            plog("next: ${cal[Calendar.DAY_OF_MONTH]}/${cal[Calendar.MONTH]};${cal[Calendar.HOUR_OF_DAY]}:${cal[Calendar.MINUTE]}")
            scheduleAlarm(
                context = context,
                alarmId = nextAlarm.id,
                bellUrl = nextAlarm.bell_url,
                bellName = nextAlarm.bell_name,
                timeInMillis = nextAlarm.time_in_millis,
            )
        } else {
            clearScheduledAlarms(context)
        }
    }
}
