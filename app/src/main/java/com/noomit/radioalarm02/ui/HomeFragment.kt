package com.noomit.radioalarm02.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.AlarmManagerViewModelFactory
import com.noomit.radioalarm02.base.BaseFragment
import com.noomit.radioalarm02.databinding.FragmentHomeBinding
import com.noomit.radioalarm02.model.AppDatabase
import com.noomit.radioalarm02.toast
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app-home").i("$message [${Thread.currentThread().name}]")

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    override val viewBinding: FragmentHomeBinding by viewBinding()

    private val alarmManager: AlarmManagerViewModel by activityViewModels {
        AlarmManagerViewModelFactory(
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
                deleteClickListener = { alarm ->
                    requireContext().toast("delete click")
                    plog("delete click: $alarm")
                },
                deleteLonglickListener = { alarm ->
                    requireContext().toast("delete long click")
                    alarmManager.delete(alarm)
                },
                dayOfWeekClickListener = { dayToSwitch, alarm ->
                    alarmManager.updateDayOfWeek(dayToSwitch, alarm)
                },
                enabledSwitchListener = { alarm, isEnabled ->
                    alarmManager.setEnabled(alarm, isEnabled)
                }
            )
            // #todo StationList restore state
//            layoutManager?.onRestoreInstanceState()
        }
    }

    override fun listenUiEvents() = with(viewBinding) {
        btnBrowseStations.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_favorites)
        }

        btnAlarmAdd.setOnClickListener {
            val timePicker = TimePickerFragment { _, hour, minute ->
                alarmManager.insert(hour, minute)
            }
            timePicker.show(childFragmentManager, "tag_time_picker")
        }

        btnBrowser.setOnClickListener {
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