package com.noomit.radioalarm02.ui.alarm_list

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.noomit.domain.entities.AlarmModel
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.alarm_fire.AlarmActivity
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmListAdapter
import com.noomit.radioalarm02.util.fragment.ContourFragment
import com.noomit.radioalarm02.util.fragment.collect
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
        collect(viewmodel.oneshotEvents) { event ->
            when (event) {
                is AlarmListEvent.Favorites -> findNavController().navigate(R.id.action_home_to_favorites)
                is AlarmListEvent.AddAlarm -> pickTimeAddAlarm()
                is AlarmListEvent.RadioBrowser -> findNavController().navigate(R.id.action_home_to_radioBrowser)
                is AlarmListEvent.HoldToDelete -> context?.toast(getString(R.string.toast_hold_to_del))
                is AlarmListEvent.SelectMelody -> findNavController().navigate(R.id.action_home_to_selectMelody)
                is AlarmListEvent.TestMelody -> startActivity(
                    AlarmActivity.composeIntent(
                        context = requireContext(),
                        id = event.alarm.id,
                        url = event.alarm.bellUrl,
                        name = event.alarm.bellName,
                        action = AlarmActivity.ACTION_TEST,
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP,
                    )
                )
                is AlarmListEvent.TimeChange -> pickTimeUpdateAlarm(event.alarm)
            }
        }
    }

    private fun pickTimeAddAlarm() = pickTime { _, hour, minute -> viewmodel.insert(hour, minute) }

    private fun pickTimeUpdateAlarm(alarm: AlarmModel) {
        pickTime { _, hour, minute -> viewmodel.updateTime(alarm, hour, minute) }
    }

    private fun pickTime(callback: TimePickerDialog.OnTimeSetListener) {
        val timePicker = TimePickerFragment(callback)
        timePicker.show(childFragmentManager, "tag_time_picker")
    }
}
