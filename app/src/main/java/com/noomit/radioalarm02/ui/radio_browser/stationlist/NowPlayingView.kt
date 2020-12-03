package com.noomit.radioalarm02.ui.radio_browser.stationlist

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.KeyEvent
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

interface IViewTheme {
    val bgColor: Int
    val textColor: Int
}

data class Theme(
    val nowPlaying: IViewTheme = object : IViewTheme {
        override val bgColor = Color.parseColor("#ffFFFFFF")
        override val textColor: Int = Color.parseColor("#FF414141")
    },
)

val appTheme = Theme()

class NowPlayingView(context: Context, attrSet: AttributeSet? = null) :
    ContourLayout(context, attrSet) {

    private val title = TextView(context).apply {
        ellipsize = TextUtils.TruncateAt.MARQUEE
        setTextColor(appTheme.nowPlaying.textColor)
    }

    private val streamUrl = TextView(context).apply {
        setTextColor(appTheme.nowPlaying.textColor)
    }

    private val country = TextView(context).apply {
        setTextColor(appTheme.nowPlaying.textColor)
    }

    private val nowPlayingIcon = ImageView(context)

    init {
        background = PaintDrawable(appTheme.nowPlaying.bgColor)
        registerBackpressListener()
        stateListAnimator = PushOnPressAnimator(this)
        elevation = 8.0f

        collapsedLayout()
    }

    private fun collapsedLayout() {
        toggleCornerRaduis(false)
        title.isVisible = true
        title.isSingleLine = true

        streamUrl.isVisible = false
        country.isVisible = false

        nowPlayingIcon.layoutBy(
            rightTo { parent.right() - 4.xdip },
            topTo { parent.top() + 2.ydip }.bottomTo { parent.bottom() - 2.ydip }
        )

        title.layoutBy(
            leftTo { parent.left() + 16.xdip }.rightTo { nowPlayingIcon.left() - 2.xdip },
            centerVerticallyTo { nowPlayingIcon.centerY() }
        )
    }

    private fun expandedLayout() {
        toggleCornerRaduis(true)
        title.isSingleLine = false
        streamUrl.isVisible = true
        country.isVisible = true

        title.updateLayoutBy(
            leftTo { parent.left() + 16.xdip }.rightTo { nowPlayingIcon.left() - 4.xdip },
            topTo { nowPlayingIcon.top() + 16.ydip }
        )
        nowPlayingIcon.updateLayoutBy(
            rightTo { parent.right() - 16.xdip }.widthOf { parent.width() / 3 },
            topTo { parent.top() + 16.ydip }.heightOf { (parent.width() / 3).toY() }
        )
        streamUrl.layoutBy(
            matchParentX(marginLeft = 16, marginRight = 16),
            topTo { nowPlayingIcon.bottom() + 8.ydip }
        )
        country.layoutBy(
            matchParentX(marginLeft = 16, marginRight = 16),
            topTo { streamUrl.bottom() + 8.ydip }
        )
    }

    override fun setSelected(selected: Boolean) {
        if (isLaidOut && selected == this.isSelected) return
        super.setSelected(selected)
        if (!selected) collapsedLayout() else expandedLayout()
    }

    private fun registerBackpressListener() {
        isFocusableInTouchMode = true
        requestFocus()
        setOnKeyListener { _, keyCode, keyEvent ->
            if (isSelected && keyCode == KeyEvent.KEYCODE_BACK && keyEvent.action == KeyEvent.ACTION_UP) {
                performClick()
            } else {
                false
            }
        }
    }

    override fun getBackground() = super.getBackground() as PaintDrawable

    private fun toggleCornerRaduis(show: Boolean) {
        val fromRadius = if (show) 0.01f else 12.0f.dip
        val toRadius = if (show) 12.0f.dip else 0.01f

        if (isLaidOut) {
            ObjectAnimator.ofFloat(fromRadius, toRadius)
                .apply { addUpdateListener { background.setCornerRadius(it.animatedValue as Float) } }
                .setDuration(200)
                .start()
        } else {
            background.setCornerRadius(toRadius)
        }
    }

    fun update(station: StationModel) {
        loadStationIcon(station)

        streamUrl.text = station.streamUrl
        country.text = station.country

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
