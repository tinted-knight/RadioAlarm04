package com.noomit.radioalarm02.ui.radio_browser.stationlist

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.data.StationModel
import com.squareup.contour.ContourLayout


interface IStationListLayout {
    val playerControll: PlayerControlView
    val playerView: PlayerView

    var delegate: StationListDelegate?

    fun setStationsAdapter(adapter: StationListAdapter)
    fun showLoading()
    fun showContent(values: List<StationModel>)
    fun nowPlaying(station: StationModel)
}

interface StationListDelegate {
    fun onClick(station: StationModel)
}

class StationListLayout(context: Context, attributeSet: AttributeSet? = null) :
    ContourLayout(context, attributeSet), IStationListLayout {

    override var delegate: StationListDelegate? = null

    private val inflater = LayoutInflater.from(context)

    override val playerControll =
        inflater.inflate(R.layout.exo_player_control_view, null) as PlayerControlView

    override val playerView = PlayerView(context).apply {
        useController = false
        isVisible = false
    }

    private val rvStationList = RecyclerView(context).apply {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        isVerticalScrollBarEnabled = true
    }

    private val loadingIndicator = ProgressBar(context)

    private val nowPlaying = TextView(context).apply {
        isVisible = false
        text = "Station name"
        ellipsize = TextUtils.TruncateAt.MARQUEE
    }

    private val nowPlayingIcon = ImageView(context)

    init {
        contourHeightMatchParent()

        val matchParentWidth = matchParentX(marginLeft = 16.dip, marginRight = 16.dip)

        playerControll.layoutBy(
            rightTo { parent.right() },
            bottomTo { parent.bottom() }
        )

        rvStationList.layoutBy(
            matchParentWidth,
            bottomTo { playerControll.top() }.topTo { parent.top() }
        )

        loadingIndicator.layoutBy(
            centerHorizontallyTo { parent.centerX() },
            centerVerticallyTo { parent.centerY() },
        )

        nowPlaying.layoutBy(
            leftTo { parent.left() + 16.xdip },
            centerVerticallyTo { playerControll.centerY() }
        )

        nowPlayingIcon.layoutBy(
            rightTo { playerControll.left() - 4.xdip },
            topTo { playerControll.top() + 4.ydip }.bottomTo { playerControll.bottom() - 4.ydip }
        )
    }

    override fun setStationsAdapter(adapter: StationListAdapter) {
        rvStationList.adapter = adapter
        adapter.onClick = {
            delegate?.onClick(it)
        }
    }

    override fun showLoading() {
        loadingIndicator.isVisible = true
        rvStationList.isVisible = false
    }

    override fun showContent(values: List<StationModel>) {
        (rvStationList.adapter as StationListAdapter).submitList(values)
        loadingIndicator.isVisible = false
        rvStationList.isVisible = true
    }

    override fun nowPlaying(station: StationModel) {
        loadStationIcon(station)

        nowPlaying.text = station.name
        nowPlaying.isVisible = true
        nowPlaying.apply {
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
