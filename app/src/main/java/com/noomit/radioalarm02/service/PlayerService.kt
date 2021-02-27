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

    companion object {
        const val PLAY_PAUSE_ACTION = "action-play-pause"
        const val STOP_ACTION = "action-stop"

        const val NOTIFICATION_ID = 42
        const val NOTIF_CHANNEL_ID = "radio-alarm-notif-ch-id"
        const val NOTIF_CHANNEL_NAME = "radio-alarm-notif-ch-name"

        const val BR_ACTION_ERROR = "com.noomit.radioalarm.service_br.error"
        const val BR_ACTION_STATE = "com.noomit.radioalarm.service_br.state"
        const val BR_MEDIA_UNAVAILABLE = "br-service-unavailable"
        const val BR_MEDIA_IS_PLAYING = "br-service-is-playing"
        const val BR_CODE_ERROR = 1

        fun intent(context: Context, action: String? = null) = Intent(context, PlayerService::class.java).apply {
            action?.let { this.action = it }
        }
    }

    private lateinit var exoPlayer: SimpleExoPlayer
    private var mediaTitle: String? = null
    private var caption: String? = null

    private var binder = PlayerServiceBinder()

    private val pintentPlayPause: PendingIntent
        get() = PendingIntent.getService(this, 0, intent(this, PLAY_PAUSE_ACTION), 0)

    private val pintentStopService: PendingIntent
        get() = PendingIntent.getService(this, 0, intent(this, STOP_ACTION), 0)

    override fun onBind(intent: Intent): IBinder {
        intent.let {
            exoPlayer.playWhenReady = false
            displayNotification()
        }
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean {
        updateNotification()
        return false
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
            when (it.action) {
                PLAY_PAUSE_ACTION -> mediaTitle?.let {
                    exoPlayer.playWhenReady = !exoPlayer.playWhenReady
                }
                STOP_ACTION -> {
                    stopService(intent(this))
                }
//                else -> exoPlayer.playWhenReady = true
            }
            updateNotification()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private val playerStateListener = object : Player.DefaultEventListener() {
        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
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

    private fun displayNotification() {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, NOTIF_CHANNEL_ID).apply {
            setContent(remoteViews)
            setContentTitle(mediaTitle)
            setContentText(if (exoPlayer.playWhenReady) getString(R.string.state_playing) else getString(R.string.state_paused))
            addAction(
                R.drawable.ic_play_arrow_24,
                if (exoPlayer.playWhenReady) getString(R.string.action_pause) else getString(R.string.action_play),
                pintentPlayPause
            )
            addAction(
                R.drawable.ic_play_arrow_24,
                getString(R.string.btn_close),
                pintentStopService
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
        displayNotification()
    }

    private val remoteViews: RemoteViews by lazy(LazyThreadSafetyMode.NONE) {
        RemoteViews(packageName, R.layout.notification_player).apply {
            setOnClickPendingIntent(R.id.tv_play_pause, pintentPlayPause)
            setOnClickPendingIntent(R.id.tv_stop, pintentStopService)
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

        fun pause() {
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
}

data class MediaItem(
    val url: String,
    val title: String,
)
