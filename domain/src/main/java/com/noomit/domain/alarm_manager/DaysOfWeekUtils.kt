package com.noomit.domain.alarm_manager

import com.noomit.domain.entities.AlarmModel
import java.util.*

object DaysOfWeek {
  val days = listOf(
    Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
    Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
  )
  const val MON_ID = 0
  const val TUE_ID = 1
  const val WED_ID = 2
  const val THU_ID = 3
  const val FRI_ID = 4
  const val SAT_ID = 5
  const val SUN_ID = 6
}

private val daysMask = listOf(1, 2, 4, 8, 16, 32, 64)

internal fun Calendar.packWeekInBits() = when (val dayOfWeek = this.get(Calendar.DAY_OF_WEEK)) {
  1 -> daysMask[6]
  else -> daysMask[dayOfWeek - 2]
}

internal fun AlarmModel.switchDayBit(day: Int) = when (day) {
  1 -> this.daysOfWeek xor daysMask[6]
  else -> this.daysOfWeek xor daysMask[day - 2]
}

/**
 * Use on daysOfWeek [Int]
 */
internal fun Int.isDayBitOn(day: Int): Boolean {
  if (day == 1) return daysMask[6] and this == daysMask[6]
  return (daysMask[day - 2] and this) == daysMask[day - 2]
}
