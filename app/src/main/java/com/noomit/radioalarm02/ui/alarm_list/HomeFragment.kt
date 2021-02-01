package com.noomit.radioalarm02.ui.alarm_list

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.alarm_fire.AlarmActivity
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmListAdapter
import com.noomit.radioalarm02.util.ContourFragment
import com.noomit.radioalarm02.util.collect
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : ContourFragment<IHomeLayout>() {

    private val viewmodel: HomeViewModel by activityViewModels()

    override val layout: View
        get() = HomeLayout(requireContext())

    override val contour: IHomeLayout
        get() = view as IHomeLayout

    override fun prepareView(savedState: Bundle?) {
        contour.setAdapter(AlarmListAdapter(viewmodel))
        contour.delegate = viewmodel
    }

    override fun observeViewModel() {
        collect(viewmodel.alarms) {
            if (it.isNotEmpty()) {
                contour.showContent(it)
            } else {
                contour.showEmpty()
            }
        }
    }

    override fun observeCommands() {
        collect(viewmodel.commands) { command ->
            when (command) {
                is AlarmListDirections.Favorites -> findNavController().navigate(R.id.action_home_to_favorites)
                is AlarmListDirections.AddAlarm -> pickTime { _, hour, minute -> viewmodel.insert(hour, minute) }
                is AlarmListDirections.RadioBrowser -> findNavController().navigate(R.id.action_home_to_radioBrowser)
                is AlarmListDirections.HoldToDelete -> context?.toast(getString(R.string.toast_hold_to_del))
                is AlarmListDirections.SelectMelody -> findNavController().navigate(R.id.action_home_to_selectMelody)
                is AlarmListDirections.TestMelody -> startActivity(AlarmActivity.composeIntent(
                    context = requireContext(),
                    id = command.alarm.id,
                    url = command.alarm.bellUrl,
                    name = command.alarm.bellName,
                    action = AlarmActivity.ACTION_TEST,
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP,
                ))
                is AlarmListDirections.TimeChange -> pickTime { _, hour, minute -> viewmodel.updateTime(command.alarm, hour, minute) }
            }
        }
    }

    private fun pickTime(callback: TimePickerDialog.OnTimeSetListener) {
        val timePicker = TimePickerFragment(callback)
        timePicker.show(childFragmentManager, "tag_time_picker")
    }
}
