package com.noomit.radioalarm02.ui.alarm_fire

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout

interface IAlarmFireLayout {
    val playerControll: PlayerControlView
    val playerView: PlayerView

    fun setStationName(value: String)
    fun setTime(value: String)
    fun setDay(value: String)
}

class AlarmFireLayout(context: Context, attrSet: AttributeSet? = null) :
    ContourLayout(context, attrSet), IAlarmFireLayout {

    private val stationName = TextView(
        context,
        null,
        appTheme.alarmFire.station.attr,
    ).apply {
        text = ""
        maxLines = 2
        ellipsize = TextUtils.TruncateAt.END
    }

    private val time = TextView(
        context,
        null,
        appTheme.alarmFire.time.attr,
    ).apply {
        background = ResourcesCompat.getDrawable(resources, R.drawable.alarm_bg_time, null)
        // text for preview
//        text = "8:40"
    }

    private val dayOfWeek = TextView(
        context,
        null,
        appTheme.alarmFire.day.attr,
    )

    override val playerControll =
        LayoutInflater.from(context)
            .inflate(R.layout.exo_player_control_view, null) as PlayerControlView

    override val playerView = PlayerView(context).apply {
        useController = false
        isVisible = false
    }

    init {
        fitsSystemWindows = true
        background = ResourcesCompat.getDrawable(resources, R.drawable.black_bg_gradient, null)
        time.layoutBy(
            centerHorizontallyTo { parent.centerX() },
            centerVerticallyTo { parent.centerY() },
        )
        stationName.layoutBy(
            matchParentX(16, 16),
            topTo { parent.top() + 8.ydip }
        )
        dayOfWeek.layoutBy(
            centerHorizontallyTo { parent.centerX() },
            topTo { time.bottom() + 16.ydip }
        )
        playerControll.layoutBy(
            emptyX(),
            emptyY()
        )
    }

    override fun setStationName(value: String) {
        stationName.text = value
    }

    override fun setTime(value: String) {
        time.text = value
    }

    override fun setDay(value: String) {
        dayOfWeek.text = value
    }
}
