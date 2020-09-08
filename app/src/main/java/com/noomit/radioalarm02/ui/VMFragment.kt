package com.noomit.radioalarm02.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.radiobrowser.RadioBrowserService
import com.noomit.radioalarm02.radiobrowserview.RadioBrowserViewModel
import com.noomit.radioalarm02.vm.ViewModelFactory

abstract class VMFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareUi()
        observeModel()
        listenUiEvents()
    }

    abstract fun prepareUi()
    abstract fun listenUiEvents()
    abstract fun observeModel()
}

abstract class RadioVMFragment(@LayoutRes contentLayoutId: Int) : VMFragment(contentLayoutId) {

    protected val viewModel: RadioBrowserViewModel by activityViewModels {
        ViewModelFactory(RadioBrowserService())
    }

}