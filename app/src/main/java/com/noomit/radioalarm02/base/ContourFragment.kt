package com.noomit.radioalarm02.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.noomit.playerservice.PlayerService

// #todo migrate to ContourFragmentNew
abstract class ContourFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareView()
        observeViewModel()
    }

    protected abstract fun prepareView()

    protected abstract fun observeViewModel()
}

abstract class ContourFragmentNew<L> : Fragment() {
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ) = layout

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareView()
        observeViewModel()
    }

    /**
     * Called at the end of [onViewCreated] before [observeViewModel]
     *
     * Typically set here recycler view adapter, layout event listeners and delegates etc.
     */
    protected abstract fun prepareView()

    /**
     * Called at the end of [onViewCreated] after [prepareView]
     *
     * Observe viewmodel and update layout
     */
    protected abstract fun observeViewModel()
}

abstract class PlayerServiceFragment : ContourFragment() {

    protected lateinit var playerView: PlayerView
    protected lateinit var playerControlView: PlayerControlView

    protected var service: PlayerService.PlayerServiceBinder? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is PlayerService.PlayerServiceBinder) {
                playerView.player = service.exoPlayerInstance
                playerControlView.player = playerView.player
                this@PlayerServiceFragment.service = service
                onServiceConnected()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }

    }

    /**
     * Fires when [service] connects to this [PlayerServiceFragment]
     * Expected that here [service] should not be null
     */
    abstract fun onServiceConnected()

    /**
     * Here [playerView] and [playerControlView] need to be initialized with
     * views in fragment's layout
     */
    protected abstract fun initPlayerViews()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initPlayerViews()
        bindExoPlayerService()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().apply {
            unbindService(connection)
            stopService(Intent(this, PlayerService::class.java))
        }
    }

    private fun bindExoPlayerService() {
        val intent = Intent(requireActivity(), PlayerService::class.java)
        requireActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }
}
