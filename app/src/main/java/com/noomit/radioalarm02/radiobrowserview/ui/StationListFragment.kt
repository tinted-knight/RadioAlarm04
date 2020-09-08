package com.noomit.radioalarm02.radiobrowserview.ui

import by.kirich1409.viewbindingdelegate.viewBinding
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.databinding.FragmentStationListBinding
import com.noomit.radioalarm02.toast
import com.noomit.radioalarm02.ui.RadioVMFragment

class StationListFragment : RadioVMFragment(R.layout.item_category) {

    private val viewBinding: FragmentStationListBinding by viewBinding()

    override fun prepareUi() {
        requireContext().toast("Station list fragment")
    }

    override fun listenUiEvents() {
    }

    override fun observeModel() {
    }
}