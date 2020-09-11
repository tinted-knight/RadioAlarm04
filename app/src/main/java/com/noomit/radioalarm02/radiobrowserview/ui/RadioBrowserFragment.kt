package com.noomit.radioalarm02.radiobrowserview.ui

import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.base.BaseFragment
import com.noomit.radioalarm02.databinding.FragmentRadioBrowserBinding

class RadioBrowserFragment() : BaseFragment(R.layout.fragment_radio_browser) {

    override val viewBinding: FragmentRadioBrowserBinding by viewBinding()

//    private val viewModel: RadioBrowserViewModel by activityViewModels()

    override fun prepareUi() {}

    override fun observeModel() {}

    override fun listenUiEvents() = with(viewBinding) {
        btnLanguages.setOnClickListener {
            findNavController().navigate(R.id.action_radioBrowser_to_languageList)
        }
    }
}