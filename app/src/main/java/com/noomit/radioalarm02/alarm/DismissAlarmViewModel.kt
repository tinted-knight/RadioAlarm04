package com.noomit.radioalarm02.alarm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.model.clearScheduledAlarms
import com.noomit.radioalarm02.model.reComposeFired
import com.noomit.radioalarm02.model.scheduleAlarm
import timber.log.Timber
import java.util.*

private fun plog(message: String) =
    Timber.tag("tagg-app-dismiss_alarm").i("$message [${Thread.currentThread().name}]")

class DismissAlarmViewModel(database: Database, application: Application) :
    AndroidViewModel(application) {

    private val queries = database.alarmQueries

    var alarmId: Long? = null
    var melodyUrl: String? = null

    init {
        plog("DismissAlarmViewModel")
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
                context = getApplication(),
                alarmId = nextAlarm.id,
                bellUrl = nextAlarm.bell_url,
                timeInMillis = nextAlarm.time_in_millis,
            )
        } else {
            clearScheduledAlarms(getApplication())
        }
    }

}