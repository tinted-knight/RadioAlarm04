package com.noomit.radioalarm02.util

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.noomit.playerservice.PlayerService

/**
 * Generic parameter [L] is supposed to be your layout's interface
 */
abstract class ContourFragment<L> : Fragment() {
    /**
     * Layout, that will just be returned by [onCreateView] method
     *
     * __Important notice__: use _get() =_ syntax
     */
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
    override fun onDestroyView() {
        view?.let { view ->
            val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        super.onDestroyView()
    }

    companion object {
        /**
         * Bundle key to save RecyclerView state
         */
        const val RECYCLER_STATE = "recycler-state"
    }
}

abstract class PlayerServiceFragment<L> : ContourFragment<L>() {

    protected lateinit var playerView: PlayerView
    protected lateinit var playerControlView: PlayerControlView

    protected var service: PlayerService.PlayerServiceBinder? = null

    private var playerBroadcastReceiver: BroadcastReceiver? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is PlayerService.PlayerServiceBinder) {
                playerView.player = service.exoPlayerInstance
                playerControlView.player = playerView.player
                this@PlayerServiceFragment.service = service
                service.setCaption(notificationCaption)
                onServiceConnected()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }

    }

    abstract val notificationCaption: String

    /**
     * Fires when [service] connects to this [PlayerServiceFragment]
     * Expected that here [service] should not be null
     */
    open fun onServiceConnected() {}

    /**
     * Here [playerView] and [playerControlView] need to be initialized with
     * views in fragment's layout
     */
    protected abstract fun initPlayerViews()

    /**
     * Called when broadcast message comes with connection error from [PlayerService]
     */
    open fun onConnectionError() {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPlayerViews()
        bindExoPlayerService()
        registerBroadcastReceiver()
    }

    override fun onDestroyView() {
        requireActivity().apply {
            requireActivity().unregisterReceiver(playerBroadcastReceiver)
            unbindService(connection)
            stopService(Intent(this, PlayerService::class.java))
        }
        super.onDestroyView()
    }

    private fun bindExoPlayerService() {
        val intent = Intent(requireActivity(), PlayerService::class.java)
        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun registerBroadcastReceiver() {
        playerBroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val codeError = intent?.getIntExtra(PlayerService.BR_MEDIA_UNAVAILABLE, -1)
                when (codeError) {
                    PlayerService.BR_CODE_ERROR -> onConnectionError()
                }
            }
        }
        requireActivity().registerReceiver(
            playerBroadcastReceiver,
            IntentFilter(PlayerService.BROADCAST_FILTER),
        )
    }
}
