package com.noomit.playerservice

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
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-player").i("$message [${Thread.currentThread().name}]")

class PlayerService : Service() {

    private lateinit var exoPlayer: SimpleExoPlayer
    private var mediaTitle: String? = null
    private var caption: String? = null

    override fun onBind(intent: Intent): IBinder {
        intent.let {
            exoPlayer.playWhenReady = false
            displayNotification()
        }
        return PlayerServiceBinder()
    }

    override fun onCreate() {
        super.onCreate()
        val trackSelection = AdaptiveTrackSelection.Factory(DefaultBandwidthMeter())
        val trackSelector = DefaultTrackSelector(trackSelection)
        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector)
        exoPlayer.addListener(playerStateListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer.release()
        hideNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.let {
            when (it.getIntExtra(PLAY_PAUSE_ACTION, -1)) {
                PLAY_PAUSE_VALUE -> mediaTitle?.let {
                    exoPlayer.playWhenReady = !exoPlayer.playWhenReady
                }
                -1 -> exoPlayer.playWhenReady = false
                else -> exoPlayer.playWhenReady = true
            }
            updateNotification()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private val playerStateListener = object : Player.DefaultEventListener() {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> updateNotification()
//                Player.STATE_IDLE -> plog("IDLE")
//                Player.STATE_BUFFERING -> plog("BUFFERING")
//                Player.STATE_ENDED -> plog("ENDED")
            }
        }

        override fun onPlayerError(error: ExoPlaybackException?) {
            val intent = Intent(BROADCAST_FILTER)
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
            setContentTitle(mediaTitle)
            setContentText(if (exoPlayer.playWhenReady) getString(R.string.state_playing) else getString(R.string.state_paused))
            addAction(
                R.drawable.ic_play_arrow_24,
                if (exoPlayer.playWhenReady) getString(R.string.action_pause) else getString(R.string.action_play),
                intentPlayPause
            )
            setSmallIcon(R.drawable.ic_radio_24)
//            setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_radio_24))
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
        displayNotification()
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
            setOnClickPendingIntent(R.id.btn_play_pause, intent)
            composeRemoteViews(this)
        }
    }

    private fun updateRemoteViews(isPlaying: Boolean = true) = composeRemoteViews(remoteViews)
    private fun composeRemoteViews(remoteViews: RemoteViews) = with(remoteViews) {
        setImageViewResource(
            R.id.btn_play_pause,
            if (exoPlayer.playWhenReady) R.drawable.ic_stop_24 else R.drawable.ic_play_arrow_24
        )
        setTextViewText(
            R.id.tv_title,
            mediaTitle ?: getString(R.string.state_playing_nothing)
        )
        setTextViewText(
            R.id.tv_caption,
            caption ?: ""
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

        const val MEDIA_URL = "media-url"
        const val NOTIFICATION_ID = 42
        const val NOTIF_CHANNEL_ID = "radio-alarm-notif-ch-id"
        const val NOTIF_CHANNEL_NAME = "radio-alarm-notif-ch-name"

        const val BROADCAST_FILTER = "com.noomit.radioalarm.service_br"
        const val BR_MEDIA_UNAVAILABLE = "br-service-unavailable"
        const val BR_CODE_ERROR = 1
    }

}

data class MediaItem(
    val url: String,
    val title: String,
)
