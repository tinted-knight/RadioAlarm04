package com.noomit.radioalarm02.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewBinding: FragmentHomeBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listenUiEvents()
    }

    private fun listenUiEvents() = with(viewBinding) {
        btnBrowseStations.setOnClickListener {
            Toast.makeText(requireContext(), "test", Toast.LENGTH_SHORT).show()
        }
    }
}