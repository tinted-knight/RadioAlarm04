package com.noomit.radioalarm02.ui.radio_browser.stationlist.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import com.noomit.domain.entities.StationModel
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.ui.animations.PushOnPressAnimator
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout

interface NowPlayingListener {
    fun onFavoriteClick()
    fun onFavoriteLongClick()
    fun onHomePageClick()
    fun onHomePageLongClick()
}

class NowPlayingView(context: Context, attrSet: AttributeSet? = null) :
    ContourLayout(context, attrSet) {

    var nowPlayingListener: NowPlayingListener? = null

    private val title = MaterialTextView(context, null, appTheme.nowPlaying.titleStyle.attr)

    private val homePage = TextView(context)

    private val country = TextView(context)

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

    private val stationPicture = ImageView(context)

    private val iconInFavorites = ContextCompat.getDrawable(context, appTheme.nowPlaying.iconFavorite)
    private val iconNotInFavorites = ContextCompat.getDrawable(context, appTheme.nowPlaying.iconNotFavorite)

    private val btnFav = ImageButton(
        ContextThemeWrapper(context, appTheme.nowPlaying.favoriteStyleId),
        null,
        appTheme.nowPlaying.favoriteStyleId
    ).apply {
        setImageDrawable(iconNotInFavorites)
        setOnClickListener { nowPlayingListener?.onFavoriteClick() }
        setOnLongClickListener {
            nowPlayingListener?.onFavoriteLongClick()
            true
        }
    }

    private val btnHomePage = ImageButton(
        ContextThemeWrapper(context, appTheme.nowPlaying.favoriteStyleId),
        null,
        appTheme.nowPlaying.favoriteStyleId
    ).apply {
        setImageDrawable(ContextCompat.getDrawable(context, appTheme.nowPlaying.iconHomepage))
        setOnClickListener { nowPlayingListener?.onHomePageClick() }
        setOnLongClickListener {
            nowPlayingListener?.onHomePageLongClick()
            true
        }
    }

    private fun buildChip(value: String) = TextView(
        context,
        null,
        appTheme.nowPlaying.tag.attr
    ).apply {
        text = value
    }

    init {
        background = PaintDrawable(getColor(resources, appTheme.nowPlaying.bgColor, null))
        registerBackpressListener()
        stateListAnimator = PushOnPressAnimator(this)

        collapsedLayout()
    }

    private fun collapsedLayout() {
//        toggleCornerRaduis(false)
        setPadding(4.dip, 2.dip, 4.dip, 2.dip)

        title.isSingleLine = true
        homePage.isVisible = false
        country.isVisible = false
        codec.isVisible = false
        bitrate.isVisible = false
        tagList.isVisible = false
        btnFav.isVisible = false
        btnHomePage.isVisible = false

        stationPicture.layoutBy(
            x = rightTo { parent.right() - 4.xdip },
            y = topTo { parent.top() + 2.ydip }.bottomTo { parent.bottom() - 2.ydip }
        )

        title.background = null
        title.setTextColor(getColor(resources, R.color.clNowPlayingTitle, null))
        title.setPadding(0)
        title.layoutBy(
            x = leftTo { parent.left() + 16.xdip }.rightTo { stationPicture.left() - 2.xdip },
            y = centerVerticallyTo { stationPicture.centerY() }
        )

        homePage.layoutBy(emptyX(), emptyY())
        country.layoutBy(emptyX(), emptyY())
        codec.layoutBy(emptyX(), emptyY())
        bitrate.layoutBy(emptyX(), emptyY())
        tagList.layoutBy(emptyX(), emptyY())
        btnFav.layoutBy(emptyX(), emptyY())
        btnHomePage.layoutBy(emptyX(), emptyY())
    }

    private fun expandedLayoutNew() {
//        toggleCornerRaduis(true)
        setPadding(16.dip, 16.dip, 16.dip, 16.dip)

        title.isSingleLine = false
        title.maxLines = 2
        homePage.isVisible = true
        country.isVisible = true
        codec.isVisible = codec.value.isNotBlank()
        bitrate.isVisible = bitrate.value.isNotBlank()
        tagList.isVisible = true
        btnFav.isVisible = true
        btnHomePage.isVisible = true

        val vSpacing = 8.ydip
        val hSpacing = 8.xdip

        title.apply {
            background = PaintDrawable(getColor(resources, R.color.clTitleBg, null)).apply {
                setCornerRadius(16.0f)
            }
            setTextColor(getColor(resources, R.color.clNowPlayingTitleExpanded, null))
            setPadding(16.dip, 8.dip, 16.dip, 8.dip)
            updateLayoutBy(
                matchParentX(),
                topTo { parent.top() }
            )
        }

        stationPicture.updateLayoutBy(
            x = leftTo { parent.left() }.widthOf { parent.width() * 2 / 5 },
            y = topTo { title.bottom() + vSpacing }.heightOf { (parent.width() * 2 / 5).toY() }
        )

        bitrate.updateLayoutBy(
            leftTo { stationPicture.right() + hSpacing }.rightTo { parent.right() },
            topTo { title.bottom() + vSpacing }
        )

        codec.updateLayoutBy(
            leftTo { stationPicture.right() + hSpacing }.rightTo { parent.right() },
            topTo { bitrate.bottom() + vSpacing }
        )
        btnFav.updateLayoutBy(
            leftTo { parent.left() },
            bottomTo { parent.bottom() }
        )
        btnHomePage.updateLayoutBy(
            leftTo { btnFav.right() + hSpacing },
            bottomTo { parent.bottom() }
        )
        tagList.updateLayoutBy(
            matchParentX(),
            topTo { stationPicture.bottom() + vSpacing }
        )
    }

    override fun setSelected(selected: Boolean) {
        if (isLaidOut && selected == this.isSelected) return
        super.setSelected(selected)
        if (!selected) collapsedLayout() else expandedLayoutNew()
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

        val fromElevation = if (show) 0.0f else 6.0f
        val toElevation = if (show) 6.0f else 0.0f

        if (isLaidOut) {
            val cornerAnimator = ObjectAnimator.ofFloat(fromRadius, toRadius)
                .apply { addUpdateListener { background.setCornerRadius(it.animatedValue as Float) } }
            val elevationAnimator = ObjectAnimator.ofFloat(fromElevation, toElevation)
                .apply { addUpdateListener { elevation = it.animatedValue as Float } }

            AnimatorSet().apply {
                when {
                    show -> playSequentially(elevationAnimator, cornerAnimator)
                    else -> playSequentially(cornerAnimator, elevationAnimator)
                }
                duration = 200
            }.start()
        } else {
            background.setCornerRadius(toRadius)
        }
    }

    // #todo when changing favorite update only icon
    fun update(station: StationModel, inFavorites: Boolean) {
        loadStationIcon(station)

        homePage.text = station.homepage
        country.text = station.country
        codec.value = station.codec
        bitrate.value = station.bitrate
        // #todo if collapsed, there is no need to create ChipGroup
        tagList.removeAllViews()
        station.tags.filter { it.isNotBlank() }.forEach { value ->
            val chip = buildChip(value)
            tagList.addView(chip)
        }

        btnFav.setImageDrawable(when (inFavorites) {
            true -> iconInFavorites
            false -> iconNotInFavorites
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

    fun updateEmpty() {
        title.text = ""
        stationPicture.setImageDrawable(null)
        homePage.text = ""
        country.text = ""
        codec.value = ""
        bitrate.value = ""
        tagList.removeAllViews()
        isSelected = false
    }

    private fun loadStationIcon(station: StationModel) {
        Glide.with(this).load(station.favicon)
            .error(R.drawable.ic_wifi_tethering_24)
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
                    stationPicture.apply {
                        alpha = 0f
                        isVisible = true
                        animate().setDuration(300L)
                            .alpha(1f)
                            .setListener(null)
                    }
                    return false
                }
            })
            .into(stationPicture)
    }
}
