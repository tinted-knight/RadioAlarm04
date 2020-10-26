package com.noomit.radioalarm02.base

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewbinding.ViewBinding
import com.noomit.playerservice.BasePlayerServiceFragment
import com.noomit.radioalarm02.radiobrowserview.viewmodels.RadioBrowserViewModel
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-app").i("$message [${Thread.currentThread().name}]")

abstract class BaseFragment(@LayoutRes contentLayoutId: Int) : Fragment(contentLayoutId) {

    protected abstract val viewBinding: ViewBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareUi()
        observeModel()
        listenUiEvents()
    }

    abstract fun prepareUi()
    abstract fun listenUiEvents()
    // #todo change to Job for StateFlow
    abstract fun observeModel()
}

// #deprecated due to bad desing
abstract class RadioVMFragment(@LayoutRes contentLayoutId: Int) : BaseFragment(contentLayoutId) {

    protected val viewModel: RadioBrowserViewModel by activityViewModels()

}

abstract class PlayerBaseFragment(
    @IdRes playerViewId: Int,
    @IdRes playerControlId: Int,
    @LayoutRes contentLayoutId: Int,
) :
    BasePlayerServiceFragment(playerViewId, playerControlId, contentLayoutId) {

    // #deprecated due to bad design
//    protected val viewModel: RadioBrowserViewModel by activityViewModels()

    protected abstract val viewBinding: ViewBinding

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
