package com.noomit.radioalarm02.ui.alarm_list

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.AlarmReceiver
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.alarm.ui.AlarmActivity
import com.noomit.radioalarm02.base.AndroidViewModelFactory
import com.noomit.radioalarm02.base.BaseFragment
import com.noomit.radioalarm02.data.AppDatabase
import com.noomit.radioalarm02.databinding.FragmentHomeBinding
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmAdapterActions
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmListAdapter

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    override val viewBinding: FragmentHomeBinding by viewBinding()

    private val alarmManager: AlarmManagerViewModel by activityViewModels {
        AndroidViewModelFactory(
            AppDatabase.getInstance(requireActivity()),
            requireActivity().application,
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenUiEvents()
        observeModel()
    }

    override fun prepareUi() {
        showLoading()
        viewBinding.rvAlarms.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            isVerticalScrollBarEnabled = true
            // #fake
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            adapter = AlarmListAdapter(
                delegate = object : AlarmAdapterActions {
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
            )
            // #todo StationList restore state
//            layoutManager?.onRestoreInstanceState()
        }
    }

    override fun listenUiEvents() = with(viewBinding) {
        bbarFavorites.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_favorites)
        }

        bbarAddAlarm.setOnClickListener {
            pickTime { _, hour, minute -> alarmManager.insert(hour, minute) }
        }

        bbarBrowse.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_radioBrowser)
        }
    }

    override fun observeModel() {
        alarmManager.alarms.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                showContent(it)
            } else {
                showEmpty()
            }
        }

    }

    private fun pickTime(callback: TimePickerDialog.OnTimeSetListener) {
        val timePicker = TimePickerFragment(callback)
        timePicker.show(childFragmentManager, "tag_time_picker")
    }

    private fun showLoading() = with(viewBinding) {
        progressIndicator.visibility = View.VISIBLE
        rvAlarms.visibility = View.INVISIBLE
    }

    private fun showContent(values: List<Alarm>) = with(viewBinding) {
        (rvAlarms.adapter as AlarmListAdapter).submitList(values)
        rvAlarms.visibility = View.VISIBLE
        progressIndicator.visibility = View.INVISIBLE
    }

    private fun showEmpty() = with(viewBinding) {
        (viewBinding.rvAlarms.adapter as AlarmListAdapter).submitList(emptyList())
        rvAlarms.visibility = View.INVISIBLE
        viewBinding.progressIndicator.visibility = View.INVISIBLE
    }
}
