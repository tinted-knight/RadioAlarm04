package com.noomit.radioalarm02.base

import android.os.Bundle
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.noomit.playerservice.XmlPlayerServiceFragment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

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

abstract class PlayerBaseFragment(
    @IdRes playerViewId: Int,
    @IdRes playerControlId: Int,
    @LayoutRes contentLayoutId: Int,
) :
    XmlPlayerServiceFragment(playerViewId, playerControlId, contentLayoutId) {

    protected abstract val viewBinding: ViewBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareUi()
        // #todo run in lifecycleScope.launchWhenStarted {...}
        observeModel()
        listenUiEvents()
    }

    override fun prepareView() {}
    override fun observeViewModel() {}

    abstract fun prepareUi()
    abstract fun listenUiEvents()
    abstract fun observeModel()
}

fun <T> Fragment.collect(values: Flow<T>, block: suspend (T) -> Unit) =
    lifecycleScope.launchWhenStarted {
        values.collect { block(it) }
    }
