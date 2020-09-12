package com.noomit.radioalarm02.ui

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.noomit.radioalarm02.toast
import java.util.*

class TimePickerFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val cal = Calendar.getInstance()
        val hour = cal[Calendar.HOUR_OF_DAY]
        val minute = cal[Calendar.MINUTE]

        return TimePickerDialog(requireActivity(), timeSetListener, hour, minute, true)
    }

    private val timeSetListener =
        TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            requireActivity().toast("Choosed: $hourOfDay : $minute")
        }
}