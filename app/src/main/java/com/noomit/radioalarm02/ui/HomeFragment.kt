package com.noomit.radioalarm02.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.BaseFragment
import com.noomit.radioalarm02.base.DatabaseViewModelFactory
import com.noomit.radioalarm02.base.ViewModelFactory
import com.noomit.radioalarm02.databinding.FragmentHomeBinding
import com.noomit.radioalarm02.model.AppDatabase
import com.noomit.radioalarm02.radiobrowserview.RadioBrowserViewModel
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app-home").i("$message [${Thread.currentThread().name}]")

class HomeFragment : BaseFragment(R.layout.fragment_home) {

    override val viewBinding: FragmentHomeBinding by viewBinding()

    private val viewModel: RadioBrowserViewModel by activityViewModels {
        ViewModelFactory(RadioBrowserService())
    }

    private val alarmManager: AlarmManagerViewModel by activityViewModels {
        DatabaseViewModelFactory(AppDatabase.getInstance(requireActivity()))
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
    }

    override fun observeModel() {
        alarmManager.alarms.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                plog("alarm list: ${it.size}")
                it.forEach { alarm -> plog(alarm.toString()) }
                showContent(it)
            } else {
                showEmpty()
            }
        }

        viewModel.availableServers.observe(viewLifecycleOwner) {
            it.fold(
                onSuccess = { values ->
                    viewBinding.tvText.text = "success"
                    viewBinding.btnServer1.apply {
                        text = values[0]
                        isEnabled = true
                        setOnClickListener {
                            viewModel.setServer(0)
                            showRadioBrowser()
                        }
                    }
                    viewBinding.btnServer2.apply {
                        text = values[1]
                        isEnabled = true
                        setOnClickListener {
                            viewModel.setServer(1)
                            showRadioBrowser()
                        }
                    }
                    viewBinding.btnServer3.apply {
                        text = values[2]
                        isEnabled = true
                        setOnClickListener {
                            viewModel.setServer(2)
                            showRadioBrowser()
                        }
                    }
                },
                onFailure = { e ->
                    viewBinding.tvText.text = "failure"
                    Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            )
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

    private fun showEmpty() {
        viewBinding.progressIndicator.visibility = View.INVISIBLE
    }

    private fun showRadioBrowser() = findNavController().navigate(R.id.action_home_to_radioBrowser)
}