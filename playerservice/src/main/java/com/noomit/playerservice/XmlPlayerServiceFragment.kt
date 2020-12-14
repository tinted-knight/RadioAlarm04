package com.noomit.playerservice

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView

abstract class XmlPlayerServiceFragment(
    @IdRes private val playerViewId: Int,
    @IdRes private val playerControlId: Int,
    @LayoutRes private val contentLayoutId: Int,
) :
    PlayerServiceFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(contentLayoutId, container, false)
    }

    override fun initPlayerViews() {
        requireNotNull(view).apply {
            playerView = findViewById(playerViewId)
            playerControlView = findViewById(playerControlId)
        }
    }
}

abstract class ContourFragment : Fragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prepareView()
        observeViewModel()
    }

    protected abstract fun prepareView()

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
     * Fires when [service] connects to this [XmlPlayerServiceFragment]
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
