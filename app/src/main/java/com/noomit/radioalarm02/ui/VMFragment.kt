package com.noomit.radioalarm02.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.noomit.playerservice.BasePlayerServiceFragment
import com.noomit.radioalarm02.radiobrowserview.RadioBrowserViewModel
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

    protected val viewModel: RadioBrowserViewModel by activityViewModels()

}

abstract class PlayerVMFragment(
    @IdRes playerViewId: Int,
    @IdRes playerControlId: Int,
    @LayoutRes contentLayoutId: Int,
) :
    BasePlayerServiceFragment(playerViewId, playerControlId, contentLayoutId) {

    protected val viewModel: RadioBrowserViewModel by activityViewModels()

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