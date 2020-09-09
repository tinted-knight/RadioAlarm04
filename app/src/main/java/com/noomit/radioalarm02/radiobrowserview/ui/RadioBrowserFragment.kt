package com.noomit.radioalarm02.radiobrowserview.ui

import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.databinding.FragmentRadioBrowserBinding
import com.noomit.radioalarm02.ui.RadioVMFragment

class RadioBrowserFragment() : RadioVMFragment(R.layout.fragment_radio_browser) {
    private val viewBinding: FragmentRadioBrowserBinding by viewBinding()

    override fun prepareUi() {}

    override fun observeModel() {}

    override fun listenUiEvents() = with(viewBinding) {
        btnLanguages.setOnClickListener {
            findNavController().navigate(R.id.action_radioBrowser_to_languageList)
        }
    }
}