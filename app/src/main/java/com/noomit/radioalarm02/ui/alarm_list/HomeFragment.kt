package com.noomit.radioalarm02.ui.alarm_list

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.noomit.playerservice.ContourFragment
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.AlarmReceiver
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.alarm.ui.AlarmActivity
import com.noomit.radioalarm02.base.AndroidViewModelFactory
import com.noomit.radioalarm02.data.AppDatabase
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmAdapterActions
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmListAdapter

class HomeFragment : ContourFragment() {

    private val alarmManager: AlarmManagerViewModel by activityViewModels {
        AndroidViewModelFactory(
            AppDatabase.getInstance(requireActivity()),
            requireActivity().application,
        )
    }

    private val contour: IHomeLayout
        get() = view as IHomeLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return HomeLayout(requireContext())
    }

    override fun prepareView() {
        contour.setAdapter(AlarmListAdapter(adapterListener))
        contour.delegate = listener
    }

    override fun observeViewModel() {
        alarmManager.alarms.observe(viewLifecycleOwner) {
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
            startActivity(
                Intent(requireActivity(), AlarmActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    action = AlarmActivity.ACTION_TEST
                    putExtra(AlarmReceiver.ALARM_ID, alarm.id)
                    putExtra(AlarmReceiver.BELL_URL, alarm.bell_url)
                }
            )
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
