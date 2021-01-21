package com.noomit.radioalarm02.ui.alarm_list

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.ContourFragment
import com.noomit.radioalarm02.base.collect
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmListAdapter
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
            }
        }
    }

    //    private val adapterListener = object : AlarmAdapterActions {
//        override fun onDeleteClick(alarm: Alarm) {
//            context?.toast(getString(R.string.toast_hold_to_del))
//        }
//
//        override fun onTimeClick(alarm: Alarm) {
//            pickTime { _, hour, minute -> alarmManager.updateTime(alarm, hour, minute) }
//        }
//
//        override fun onMelodyClick(alarm: Alarm) {
//            alarmManager.selectMelodyFor(alarm)
//            findNavController().navigate(R.id.action_home_to_selectMelody)
//        }
//
//        override fun onMelodyLongClick(alarm: Alarm) {
//            startActivity(AlarmActivity.composeIntent(
//                context = requireContext(),
//                id = alarm.id,
//                url = alarm.bell_url,
//                name = alarm.bell_name,
//                action = AlarmActivity.ACTION_TEST,
//                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP,
//            ))
//        }
//    }
//
    private fun pickTime(callback: TimePickerDialog.OnTimeSetListener) {
        val timePicker = TimePickerFragment(callback)
        timePicker.show(childFragmentManager, "tag_time_picker")
    }
}
