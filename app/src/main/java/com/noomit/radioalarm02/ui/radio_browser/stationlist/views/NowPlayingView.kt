package com.noomit.radioalarm02.ui.radio_browser.stationlist.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.noomit.radioalarm02.data.StationModel
import com.noomit.radioalarm02.ui.radio_browser.stationlist.PushOnPressAnimator
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout

class NowPlayingView(context: Context, attrSet: AttributeSet? = null) :
    ContourLayout(context, attrSet) {

    private val title = TextView(context).apply {
        ellipsize = TextUtils.TruncateAt.MARQUEE
        setTextColor(appTheme.nowPlaying.textColor)
    }

    private val homePage = TextView(context).apply {
        setTextColor(appTheme.nowPlaying.textColor)
    }

    private val country = TextView(context).apply {
        setTextColor(appTheme.nowPlaying.textColor)
    }

    private val codec = LabeledView(context).apply {
        label = "Codec:"
    }

    private val bitrate = LabeledView(context).apply {
        label = "Bitrate:"
    }

    private val tagList = ChipGroup(context).apply {
        chipSpacingHorizontal = 4
        chipSpacingVertical = 4
    }

    private val nowPlayingIcon = ImageView(context)

    val btnFav = ImageButton(
        ContextThemeWrapper(context, appTheme.nowPlaying.favoriteStyleId),
        null,
        appTheme.nowPlaying.favoriteStyleId
    ).apply {
        setImageResource(appTheme.nowPlaying.iconNotFavorite)
    }

    private fun buildChip(value: String) = Chip(context).apply {
        text = value
        textSize = 12.0f
        setEnsureMinTouchTargetSize(false)
    }

    init {
        background = PaintDrawable(appTheme.nowPlaying.bgColor)
        registerBackpressListener()
        stateListAnimator = PushOnPressAnimator(this)
        elevation = 6.0f

        collapsedLayout()
    }

    private fun collapsedLayout() {
        toggleCornerRaduis(false)

        title.isSingleLine = true
        homePage.isVisible = false
        country.isVisible = false
        codec.isVisible = false
        bitrate.isVisible = false
        tagList.isVisible = false
        btnFav.isVisible = false

        nowPlayingIcon.layoutBy(
            x = rightTo { parent.right() - 4.xdip },
            y = topTo { parent.top() + 2.ydip }.bottomTo { parent.bottom() - 2.ydip }
        )
        title.layoutBy(
            x = leftTo { parent.left() + 16.xdip }.rightTo { nowPlayingIcon.left() - 2.xdip },
            y = centerVerticallyTo { nowPlayingIcon.centerY() }
        )
        homePage.layoutBy(emptyX(), emptyY())
        country.layoutBy(emptyX(), emptyY())
        codec.layoutBy(emptyX(), emptyY())
        bitrate.layoutBy(emptyX(), emptyY())
        tagList.layoutBy(emptyX(), emptyY())
        btnFav.layoutBy(emptyX(), emptyY())
    }

    private fun expandedLayout() {
        toggleCornerRaduis(true)

        title.isSingleLine = false
        homePage.isVisible = true
        country.isVisible = true
        codec.isVisible = true
        bitrate.isVisible = true
        tagList.isVisible = true
        btnFav.isVisible = true

        val fillParentWidth = matchParentX(marginLeft = 16, marginRight = 16)
        val verticalSpacing = 8.ydip

        title.updateLayoutBy(
            x = leftTo { parent.left() + 16.xdip }.rightTo { nowPlayingIcon.left() - 4.xdip },
            y = topTo { nowPlayingIcon.top() + 16.ydip }
        )
        nowPlayingIcon.updateLayoutBy(
            x = rightTo { parent.right() - 16.xdip }.widthOf { parent.width() / 3 },
            y = topTo { parent.top() + 16.ydip }.heightOf { (parent.width() / 3).toY() }
        )
        homePage.updateLayoutBy(
            x = fillParentWidth,
            y = topTo { nowPlayingIcon.bottom() + verticalSpacing }
        )
        country.updateLayoutBy(
            x = fillParentWidth,
            y = topTo { homePage.bottom() + verticalSpacing }
        )
        codec.updateLayoutBy(
            x = leftTo { country.left() },
            y = topTo { country.bottom() + verticalSpacing }
        )
        bitrate.updateLayoutBy(
            x = leftTo { codec.right() + 16.xdip },
            y = topTo { country.bottom() + 8.ydip }
        )
        tagList.updateLayoutBy(
            x = fillParentWidth,
            y = topTo { bitrate.bottom() + verticalSpacing }
        )
        btnFav.updateLayoutBy(
            x = rightTo { parent.right() - 16.xdip },
            y = bottomTo { parent.bottom() - 16.ydip }
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

    fun update(station: StationModel, inFavorites: Boolean) {
        loadStationIcon(station)

        homePage.text = station.homepage
        country.text = station.country
        codec.value = station.codec
        bitrate.value = station.bitrate
        // #todo if collapsed, there is no need to create ChipGroup
        tagList.removeAllViews()
        station.tags.filter { it.isNotBlank() }.forEach {
            val chip = buildChip(it)
            tagList.addView(chip)
        }

        btnFav.setImageResource(when (inFavorites) {
            true -> appTheme.nowPlaying.iconFavorite
            false -> appTheme.nowPlaying.iconNotFavorite
        })

        title.text = station.name
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
