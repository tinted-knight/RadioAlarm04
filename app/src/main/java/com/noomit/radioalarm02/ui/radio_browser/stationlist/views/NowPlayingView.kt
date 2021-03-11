package com.noomit.radioalarm02.ui.radio_browser.stationlist.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.PaintDrawable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.KeyEvent
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textview.MaterialTextView
import com.noomit.domain.entities.StationModel
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.ui.animations.PushOnPressAnimator
import com.noomit.radioalarm02.ui.animations.TitleTransition
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

    private val title = MaterialTextView(context, null, appTheme.nowPlaying.titleStyle.attr).apply {
        background = PaintDrawable(getColor(resources, R.color.clTitleBg, null))
        elevation = 4.0f
    }

    private val homePage = TextView(context).apply {
        setTextColor(getColor(resources, appTheme.nowPlaying.linkColor, null))
        setOnClickListener { nowPlayingListener?.onHomePageClick() }
    }

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

    private val iconInFavorites = loadResourceIcon(appTheme.nowPlaying.iconFavorite)
    private val iconNotInFavorites = loadResourceIcon(appTheme.nowPlaying.iconNotFavorite)

    private val btnFav = ImageButton(
        ContextThemeWrapper(context, appTheme.nowPlaying.favoriteStyleId),
        null,
        appTheme.nowPlaying.favoriteStyleId
    ).apply {
        setOnClickListener { nowPlayingListener?.onFavoriteClick() }
        setOnLongClickListener {
            nowPlayingListener?.onFavoriteLongClick()
            true
        }
    }

    private val btnClose = MaterialButton(
        ContextThemeWrapper(context, appTheme.nowPlaying.btnClose.style),
        null,
        appTheme.nowPlaying.btnClose.attr,
    ).apply {
        text = resources.getString(R.string.btn_close)
        setOnClickListener { this@NowPlayingView.performClick() }
    }

    private val btnFavorite = MaterialButton(
        ContextThemeWrapper(context, appTheme.nowPlaying.btnFavorite.style),
        null,
        appTheme.nowPlaying.btnFavorite.attr,
    ).apply {
        text = resources.getString(R.string.favorites)
        setOnClickListener { nowPlayingListener?.onFavoriteClick() }
        setOnLongClickListener {
            nowPlayingListener?.onFavoriteLongClick()
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
        setPadding(4.dip, 2.dip, 4.dip, 2.dip)

        title.isSingleLine = true
        homePage.isVisible = false
        country.isVisible = false
        codec.isVisible = false
        bitrate.isVisible = false
        tagList.isVisible = false
        btnClose.isVisible = false
        btnFav.isVisible = false
        btnFavorite.isVisible = false

        stationPicture.layoutBy(
            x = rightTo { parent.right() - 4.xdip },
            y = topTo { parent.top() + 2.ydip }.bottomTo { parent.bottom() - 2.ydip }
        )

        title.apply {
            background.alpha = 0
            (background as PaintDrawable).setCornerRadius(0.1f)

            setTextColor(getColor(resources, R.color.clNowPlayingTitle, null))
            setPadding(0)
            textAlignment = TEXT_ALIGNMENT_TEXT_START
            layoutBy(
                x = leftTo { parent.left() + 16.xdip }.rightTo { stationPicture.left() - 2.xdip },
                y = centerVerticallyTo { stationPicture.centerY() }
            )
        }

        homePage.layoutBy(emptyX(), emptyY())
        country.layoutBy(emptyX(), emptyY())
        codec.layoutBy(emptyX(), emptyY())
        bitrate.layoutBy(emptyX(), emptyY())
        tagList.layoutBy(emptyX(), emptyY())
        btnFav.layoutBy(emptyX(), emptyY())
        btnFavorite.layoutBy(emptyX(), emptyY())
        btnClose.layoutBy(emptyX(), emptyY())
    }

    private fun expandedLayout() {
        setPadding(16.dip, 16.dip, 16.dip, 16.dip)

        title.isSingleLine = false
        title.maxLines = 2
        homePage.isVisible = true
        country.isVisible = true
        codec.isVisible = codec.value.isNotBlank()
        bitrate.isVisible = bitrate.value.isNotBlank()
        tagList.isVisible = true
        btnClose.isVisible = true
        btnFavorite.isVisible = true

        val vSpacing = 8.ydip
        val hSpacing = 8.xdip

        title.apply {
            background.alpha = 255
            (background as PaintDrawable).setCornerRadius(16.0f)

            textAlignment = TEXT_ALIGNMENT_CENTER
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
        homePage.updateLayoutBy(
            leftTo { parent.left() }.rightTo { parent.right() },
            topTo { stationPicture.bottom() + vSpacing }
        )
        btnClose.updateLayoutBy(
            leftTo { parent.left() },
            bottomTo { parent.bottom() }
        )
        btnFavorite.updateLayoutBy(
            rightTo { parent.right() },
            centerVerticallyTo { btnClose.centerY() }
        )
        tagList.updateLayoutBy(
            matchParentX(),
            topTo { homePage.bottom() + vSpacing }
        )
    }

    val layoutTransition: Transition
        get() {
            val transitionDuration = 400L
            if (!isSelected) {
                // expanding
                return TransitionSet()
                    .addTransition(ChangeBounds().apply {
                        duration = transitionDuration
                        interpolator = OvershootInterpolator(1f)
                    })
                    .addTransition(TitleTransition(collapse = false).apply {
                        addTarget(title)
                        duration = transitionDuration
                        interpolator = FastOutSlowInInterpolator()
                    })
                    .addTransition(Fade().apply {
                        addTarget(bitrate)
                        addTarget(codec)
                        addTarget(tagList)
                        addTarget(btnClose)
                        addTarget(btnFavorite)
                        addTarget(homePage)
                        startDelay = transitionDuration / 4
                        duration = transitionDuration - startDelay
                    })
            } else {
                // collapsing
                return TransitionSet()
                    .addTransition(ChangeBounds().apply {
                        duration = transitionDuration
                        interpolator = FastOutSlowInInterpolator()
                    })
                    .addTransition(TitleTransition(collapse = true).apply {
                        addTarget(title)
                        duration = transitionDuration
                        interpolator = FastOutSlowInInterpolator()
                    })
                    .addTransition(Fade().apply {
                        addTarget(bitrate)
                        addTarget(codec)
                        addTarget(tagList)
                        addTarget(btnClose)
                        addTarget(btnFavorite)
                        addTarget(homePage)
                        duration = transitionDuration / 2
                        interpolator = LinearInterpolator()
                    })
            }
        }

    override fun setSelected(selected: Boolean) {
        if (isLaidOut && selected == this.isSelected) return
        if (title.text.isNullOrBlank()) return
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

    // #todo when changing favorite update only icon
    fun update(station: StationModel, inFavorites: Boolean) {
        loadStationIcon(station)

        homePage.text = SpannableString(station.homepage).apply {
            setSpan(UnderlineSpan(), 0, length, 0)
        }
        country.text = station.country
        codec.value = station.codec
        bitrate.value = station.bitrate
        codec.value = if (station.codec.isNotBlank()) station.codec else "MP42"
        bitrate.value = if (station.bitrate.isNotBlank()) station.bitrate else "2077"
        // #todo if collapsed, there is no need to create ChipGroup
        tagList.removeAllViews()
        station.tags.filter { it.isNotBlank() }.forEach { value ->
            val chip = buildChip(value)
            tagList.addView(chip)
        }

        btnFavorite.setCompoundDrawables(when (inFavorites) {
            true -> iconInFavorites
            false -> iconNotInFavorites
        }, null, null, null)

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

    private fun loadResourceIcon(id: Int): Drawable? {
        return ResourcesCompat.getDrawable(resources, id, null).apply {
            this?.setBounds(0, 0, 40, 40)
            this?.setTint(getColor(resources, R.color.clNowPlayingFavIcon, null))
        };
    }
}
