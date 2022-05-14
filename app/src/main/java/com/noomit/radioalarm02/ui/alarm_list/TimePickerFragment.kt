package com.noomit.radioalarm02.ui.alarm_list

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment(
  private val timeSetListener: TimePickerDialog.OnTimeSetListener,
) : DialogFragment() {
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val cal = Calendar.getInstance()
    val hour = cal[Calendar.HOUR_OF_DAY]
    val minute = cal[Calendar.MINUTE]

    return TimePickerDialog(requireActivity(), timeSetListener, hour, minute, true)
  }
}
