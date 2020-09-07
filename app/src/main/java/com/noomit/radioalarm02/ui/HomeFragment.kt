package com.noomit.radioalarm02.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.databinding.FragmentHomeBinding
import com.noomit.radioalarm02.radiobrowserview.RadioBrowserViewModel
import com.noomit.radioalarm02.vm.ViewModelFactory

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewBinding: FragmentHomeBinding by viewBinding()
    private val viewModel: RadioBrowserViewModel by activityViewModels {
        ViewModelFactory(RadioBrowserService())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenUiEvents()
        observeModel()
    }

    private fun listenUiEvents() = with(viewBinding) {
        btnBrowseStations.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_radioBrowser)
        }
    }

    private fun observeModel() {
        viewModel.availableServers.observe(viewLifecycleOwner) {
            it.fold(
                onSuccess = { values ->
                    viewBinding.btnServer1.apply {
                        text = values[0]
                        isEnabled = true
                        setOnClickListener { viewModel.setServer(0) }
                    }
                    viewBinding.btnServer2.apply {
                        text = values[1]
                        isEnabled = true
                        setOnClickListener { viewModel.setServer(1) }
                    }
                    viewBinding.btnServer3.apply {
                        text = values[2]
                        isEnabled = true
                        setOnClickListener { viewModel.setServer(2) }
                    }
                },
                onFailure = { e ->
                    Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}