package com.noomit.radioalarm02.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.databinding.FragmentHomeBinding
import com.noomit.radioalarm02.radiobrowserview.RadioBrowserViewModel
import com.noomit.radioalarm02.vm.ViewModelFactory

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewBinding: FragmentHomeBinding by viewBinding()

    private val viewModel: RadioBrowserViewModel by viewModels {
        ViewModelFactory(
            RadioBrowserService()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenUiEvents()
        observeModel()
    }

    private fun listenUiEvents() = with(viewBinding) {
        btnBrowseStations.setOnClickListener {
            Toast.makeText(requireContext(), "test", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeModel() {
        viewModel.availableServers.observe(viewLifecycleOwner) {
            viewBinding.tvText.text = it.toString()
        }
    }
}