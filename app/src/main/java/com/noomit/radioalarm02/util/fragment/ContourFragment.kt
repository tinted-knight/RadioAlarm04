package com.noomit.radioalarm02.util.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

/**
 * Generic parameter [L] is supposed to be your layout's interface
 */
abstract class ContourFragment<L> : Fragment() {
    companion object {
        /**
         * Bundle key to save RecyclerView state
         */
        const val RECYCLER_STATE = "recycler-state"
    }

    /**
     * Layout, that will just be returned by [onCreateView] method
     *
     * __Important notice__: use _get() =_ syntax
     */
    // #todo something like this:
    //  protected abstract layout: (Context) -> View
    //  and then invoke in onCreateView
    protected abstract val layout: View

    /**
     * Property to access layout, most likely something like this:
     * ```kotlin
     * val contour: ILayoutInterface
     *  get() = this.view as ILayoutInterface
     * ```
     * __Important notice__: always use _get() =_ syntax instead of
     *
     * ```val contour: L = view as L```
     *
     * to avoid exceptions when fragment's view was recreated
     */
    protected abstract val contour: L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeCommands()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareView(savedInstanceState)
        observeViewModel()
    }

    /**
     * Typically set here RecyclerView adapter, layout event listeners, delegates etc.
     *
     * Called at the end of [onViewCreated] before [observeViewModel]
     */
    protected abstract fun prepareView(savedState: Bundle?)

    /**
     * Observe viewmodel and update layout
     *
     * Called at the end of [onViewCreated] after [prepareView]
     */
    protected abstract fun observeViewModel()

    /**
     * Observe navigation commands
     *
     * Called in the [onCreate]
     */
    protected open fun observeCommands() {}

    /**
     * Hides software keyboard when fragment view is going to be destroyed
     */
    override fun onPause() {
        view?.let { view ->
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        super.onPause()
    }
}
