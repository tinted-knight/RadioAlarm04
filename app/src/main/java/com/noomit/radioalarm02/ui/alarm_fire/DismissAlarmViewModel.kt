package com.noomit.radioalarm02.ui.alarm_fire

import androidx.lifecycle.ViewModel
import com.noomit.domain.alarm_manager.DismissAlarmManager
import com.noomit.domain.alarm_manager.alarm_composer.AlarmComposer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class DismissAlarmViewModel @Inject constructor(
  private val manager: DismissAlarmManager,
  private val alarmComposer: AlarmComposer,
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

  fun alarmFired() = alarmId?.let {
    val alarm = manager.selectById(it)
    val updated = alarmComposer.reComposeFired(alarm)
    manager.updateTimeInMillis(
      id = updated.id,
      timeInMillis = updated.timeInMillis,
    )
    manager.scheduleNextActive()
  }
}
