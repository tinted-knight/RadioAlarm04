package com.noomit.radioalarm02.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.radiobrowser.RadioBrowserService
import com.noomit.playerservice.BasePlayerServiceFragment
import com.noomit.radioalarm02.radiobrowserview.RadioBrowserViewModel
import com.noomit.radioalarm02.vm.ViewModelFactory
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app").i("$message [${Thread.currentThread().name}]")

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
        plog("RadioVMFragment::viewModel")
        ViewModelFactory(RadioBrowserService())
    }

}

abstract class PlayerVMFragment(@IdRes playerViewId: Int, @LayoutRes contentLayoutId: Int) :
    BasePlayerServiceFragment(playerViewId, contentLayoutId) {

    protected val viewModel: RadioBrowserViewModel by activityViewModels {
        plog("PlayerVMFragment::viewModel")
        ViewModelFactory(RadioBrowserService())
    }

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