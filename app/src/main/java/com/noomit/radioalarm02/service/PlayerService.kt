package com.noomit.radioalarm02.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.noomit.radioalarm02.R

class PlayerService : Service() {

    private lateinit var exoPlayer: SimpleExoPlayer
    private var mediaTitle: String? = null
    private var caption: String? = null

    private var binder = PlayerServiceBinder()

    //  #todo may need to save PlayerServiceBinder in field
    //      and release it in onUnbind
    //      LeakCanary shows Binder memory leak
    override fun onBind(intent: Intent): IBinder {
        Log.d("tagg", "PlayerService.onBind")
        intent.let {
            exoPlayer.playWhenReady = false
            displayNotification(remoteViews)
        }
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        Log.d("tagg", "PlayerService::onCreate")
        super.onCreate()
        val trackSelection = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
        val trackSelector = DefaultTrackSelector(trackSelection)
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        exoPlayer.addListener(playerStateListener)
    }

    override fun onDestroy() {
        Log.d("tagg", "PlayerService::onDestroy")
        super.onDestroy()
        exoPlayer.release()
        hideNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("tagg", "PlayerService.onStartCommand")
        intent?.let {
            when (it.getIntExtra(PLAY_PAUSE_ACTION, -1)) {
                PLAY_PAUSE_VALUE -> mediaTitle?.let {
                    exoPlayer.playWhenReady = !exoPlayer.playWhenReady
                }
//                -1 -> exoPlayer.playWhenReady = false
                else -> exoPlayer.playWhenReady = true
            }
            updateNotification()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private val playerStateListener = object : Player.DefaultEventListener() {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            Log.d("tagg", "PlayerService.onPlayerStateChanged, $playbackState")
            when (playbackState) {
                Player.STATE_READY -> {
                    updateNotification()
                    val intent = Intent(BR_ACTION_STATE)
                    intent.putExtra(BR_MEDIA_IS_PLAYING, exoPlayer.playWhenReady)
                    sendBroadcast(intent)
                }
//                Player.STATE_IDLE -> plog("IDLE")
//                Player.STATE_BUFFERING -> plog("BUFFERING")
//                Player.STATE_ENDED -> plog("ENDED")
            }
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            val intent = Intent(BR_ACTION_ERROR)
            intent.putExtra(BR_MEDIA_UNAVAILABLE, BR_CODE_ERROR)
            sendBroadcast(intent)
        }
    }

    private fun hideNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.deleteNotificationChannel(NOTIF_CHANNEL_NAME)
        }
        manager.cancelAll()
    }

    private fun displayNotification(content: RemoteViews = this.remoteViews) {
        val intentPlayPause = PendingIntent.getService(
            this,
            0,
            Intent(this, PlayerService::class.java).apply {
                putExtra(PLAY_PAUSE_ACTION, PLAY_PAUSE_VALUE)
            },
            0,
        )
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, NOTIF_CHANNEL_ID).apply {
            setContent(content)
            setContentTitle(mediaTitle)
            setContentText(if (exoPlayer.playWhenReady) getString(R.string.state_playing) else getString(R.string.state_paused))
            addAction(
                R.drawable.ic_play_arrow_24,
                if (exoPlayer.playWhenReady) getString(R.string.action_pause) else getString(R.string.action_play),
                intentPlayPause
            )
            setSmallIcon(R.drawable.ic_radio_24)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(
                    NOTIF_CHANNEL_ID,
                    NOTIF_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
                )
            )
            builder.setChannelId(NOTIF_CHANNEL_ID)
        }
        val notification = builder.build()
        startForeground(NOTIFICATION_ID, notification)
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun updateNotification() {
        updateRemoteViews()
        displayNotification(remoteViews)
    }

    private val remoteViews: RemoteViews by lazy(LazyThreadSafetyMode.NONE) {
        val intent = PendingIntent.getService(
            this,
            0,
            Intent(this, PlayerService::class.java).apply {
                putExtra(PLAY_PAUSE_ACTION, PLAY_PAUSE_VALUE)
            },
            0,
        )
        RemoteViews(packageName, R.layout.notification_player).apply {
            setOnClickPendingIntent(R.id.tv_play_pause, intent)
            composeRemoteViews(this)
        }
    }

    private fun updateRemoteViews() = composeRemoteViews(remoteViews)
    private fun composeRemoteViews(remoteViews: RemoteViews) = with(remoteViews) {
        setTextViewText(
            R.id.tv_title,
            mediaTitle ?: getString(R.string.state_playing_nothing)
        )
        setTextViewText(
            R.id.tv_caption,
            caption ?: ""
        )
        setTextViewText(
            R.id.tv_play_pause,
            getString(if (exoPlayer.playWhenReady) R.string.action_pause else R.string.action_play)
        )
    }

    private fun playMedia(mediaUrl: String) {
        val dataSourceFactory = DefaultDataSourceFactory(
            this,
            Util.getUserAgent(this, javaClass.simpleName),
            DefaultBandwidthMeter()
        )
        val mediaSource = ExtractorMediaSource.Factory(dataSourceFactory)
            .setExtractorsFactory(DefaultExtractorsFactory())
            .createMediaSource(Uri.parse(mediaUrl))
        exoPlayer.prepare(mediaSource, true, false)
        updateNotification()
    }

    inner class PlayerServiceBinder : Binder() {
        val exoPlayerInstance: SimpleExoPlayer
            get() = exoPlayer

        fun play() {
            exoPlayer.playWhenReady = true
            updateNotification()
        }

        fun stop() {
            exoPlayer.playWhenReady = false
            updateNotification()
        }

        // #achtung
        fun stopService() {
            exoPlayer.playWhenReady = false
            stopSelf()
        }

        var mediaItem = MediaItem("", "")
            set(value) {
                mediaTitle = value.title
                playMedia(value.url)
                field = mediaItem
            }

        fun setCaption(value: String) {
            caption = value
            updateRemoteViews()
        }
    }

    companion object {
        const val PLAY_PAUSE_ACTION = "action-play-pause"
        const val PLAY_PAUSE_VALUE = 1001

        const val NOTIFICATION_ID = 42
        const val NOTIF_CHANNEL_ID = "radio-alarm-notif-ch-id"
        const val NOTIF_CHANNEL_NAME = "radio-alarm-notif-ch-name"

        const val BR_ACTION_ERROR = "com.noomit.radioalarm.service_br.error"
        const val BR_ACTION_STATE = "com.noomit.radioalarm.service_br.state"
        const val BR_MEDIA_UNAVAILABLE = "br-service-unavailable"
        const val BR_MEDIA_IS_PLAYING = "br-service-is-playing"
        const val BR_CODE_ERROR = 1
    }

}

data class MediaItem(
    val url: String,
    val title: String,
)
