package com.noomit.radioalarm02.ui.radio_browser.stationlist

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.noomit.radioalarm02.data.StationModel
import com.squareup.contour.ContourLayout
import timber.log.Timber

private fun plog(message: String) = Timber.tag("tagg-contour").i(message)


class NowPlayingView(context: Context, attrSet: AttributeSet? = null) :
    ContourLayout(context, attrSet) {

    private val title = TextView(context).apply {
        isVisible = false
        text = "Station name"
        ellipsize = TextUtils.TruncateAt.MARQUEE
    }

    private val nowPlayingIcon = ImageView(context)

    init {
        stateListAnimator = PushOnPressAnimator(this)
        setBackgroundColor(Color.RED)

        nowPlayingIcon.layoutBy(
            rightTo { parent.right() - 4.xdip },
            topTo { parent.top() + 2.ydip }.bottomTo { parent.bottom() - 2.ydip }
        )

        title.layoutBy(
            leftTo { parent.left() + 16.xdip },
            centerVerticallyTo { nowPlayingIcon.centerY() }
        )
    }

    override fun setSelected(selected: Boolean) {
        if (isLaidOut && selected == this.isSelected) return
        super.setSelected(selected)
    }

    fun update(station: StationModel) {
        loadStationIcon(station)

        title.text = station.name
        title.isVisible = true
        title.apply {
            alpha = 0f
            translationY = 50f
            isVisible = true
            animate().setDuration(300L)
                .alpha(1f)
                .translationY(0f)
                .setInterpolator(OvershootInterpolator())
                .setListener(null)
        }
    }


    private fun loadStationIcon(station: StationModel) {
        Glide.with(this).load(station.favicon)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean,
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean,
                ): Boolean {
                    nowPlayingIcon.apply {
                        alpha = 0f
                        isVisible = true
                        animate().setDuration(300L)
                            .alpha(1f)
                            .setListener(null)
                    }
                    return false
                }
            })
            .into(nowPlayingIcon)
    }
}
