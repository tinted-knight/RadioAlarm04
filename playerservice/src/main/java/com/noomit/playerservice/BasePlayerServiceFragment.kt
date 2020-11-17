package com.noomit.playerservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView

abstract class BasePlayerServiceFragment(
    @IdRes private val playerViewId: Int,
    @IdRes private val playerControlId: Int,
    @LayoutRes contentLayoutId: Int,
) :
    Fragment(contentLayoutId) {

    protected lateinit var playerView: PlayerView
    protected lateinit var playerControlView: PlayerControlView

    protected var service: PlayerService.PlayerServiceBinder? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (service is PlayerService.PlayerServiceBinder) {
                playerView.player = service.exoPlayerInstance
                playerControlView.player = playerView.player
                this@BasePlayerServiceFragment.service = service
                onServiceConnected()
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }

    }

    /**
     * Fires when [service] connects to this [BasePlayerServiceFragment]
     * Expected that here [service] should not be null
     */
    abstract fun onServiceConnected()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bindExoPlayerService()
        playerView = view.findViewById(playerViewId)
        playerControlView = view.findViewById(playerControlId)
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
