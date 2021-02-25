package com.noomit.radioalarm02.util.fragment

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import com.google.android.exoplayer2.ui.PlayerControlView
import com.noomit.radioalarm02.service.PlayerService

abstract class PlayerServiceFragment<L>() : ContourFragment<L>() {

    protected var playerControlView: PlayerControlView? = null

    protected var service: PlayerService.PlayerServiceBinder? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is PlayerService.PlayerServiceBinder) {
                playerControlView?.player = service.exoPlayerInstance
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

    override fun onStart() {
        super.onStart()
        bindExoPlayerService()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPlayerViews()
    }

    override fun onDestroyView() {
        playerControlView?.player = null
        playerControlView = null
        super.onDestroyView()
    }

    override fun onStop() {
        requireActivity().unbindService(connection)
        super.onStop()
    }

    private fun bindExoPlayerService() {
        requireActivity().apply {
            val intent = PlayerService.intent(this)
            application.startService(intent)
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }
}
