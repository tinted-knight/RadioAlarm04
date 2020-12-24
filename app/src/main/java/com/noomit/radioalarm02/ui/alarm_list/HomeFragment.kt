package com.noomit.radioalarm02.ui.alarm_list

import android.app.TimePickerDialog
import android.content.Intent
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.AndroidViewModelFactory
import com.noomit.radioalarm02.base.ContourFragment
import com.noomit.radioalarm02.base.collect
import com.noomit.radioalarm02.data.AppDatabase
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.alarm_fire.AlarmActivity
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmAdapterActions
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmListAdapter

class HomeFragment : ContourFragment<IHomeLayout>() {

    private val alarmManager: AlarmManagerViewModel by activityViewModels {
        AndroidViewModelFactory(
            AppDatabase.getInstance(requireActivity()),
            requireActivity().application,
        )
    }

    override val layout: View
        get() = HomeLayout(requireContext())

    override val contour: IHomeLayout
        get() = view as IHomeLayout

    override fun prepareView() {
        contour.setAdapter(AlarmListAdapter(adapterListener))
        contour.delegate = listener
    }

    override fun observeViewModel() {
        collect(alarmManager.alarms) {
            if (it.isNotEmpty()) {
                contour.showContent(it)
            } else {
                contour.showEmpty()
            }
        }
    }

    private val listener: IHomeLayoutDelegate = object : IHomeLayoutDelegate {
        override fun onFavoriteClick() {
            findNavController().navigate(R.id.action_home_to_favorites)
        }

        override fun onAddAlarmClick() {
            pickTime { _, hour, minute -> alarmManager.insert(hour, minute) }
        }

        override fun onBrowseClick() {
            findNavController().navigate(R.id.action_home_to_radioBrowser)
        }

    }

    private val adapterListener = object : AlarmAdapterActions {
        override fun onDeleteClick(alarm: Alarm) {
            requireContext().toast("delete click")
        }

        override fun onDeleteLongClick(alarm: Alarm) {
            requireContext().toast("delete long click")
            alarmManager.delete(alarm)
        }

        override fun onEnabledChecked(alarm: Alarm, isChecked: Boolean) =
            alarmManager.setEnabled(alarm, isChecked)

        override fun onTimeClick(alarm: Alarm) {
            pickTime { _, hour, minute -> alarmManager.updateTime(alarm, hour, minute) }
        }

        override fun onMelodyClick(alarm: Alarm) {
            alarmManager.selectMelodyFor(alarm)
            findNavController().navigate(R.id.action_home_to_selectMelody)
        }

        override fun onMelodyLongClick(alarm: Alarm) {
            startActivity(AlarmActivity.composeIntent(
                context = requireContext(),
                id = alarm.id,
                url = alarm.bell_url,
                name = alarm.bell_name,
                action = AlarmActivity.ACTION_TEST,
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP,
            ))
        }

        override fun onDayOfWeekClick(day: Int, alarm: Alarm) {
            alarmManager.updateDayOfWeek(day, alarm)
        }
    }

    private fun pickTime(callback: TimePickerDialog.OnTimeSetListener) {
        val timePicker = TimePickerFragment(callback)
        timePicker.show(childFragmentManager, "tag_time_picker")
    }
}
