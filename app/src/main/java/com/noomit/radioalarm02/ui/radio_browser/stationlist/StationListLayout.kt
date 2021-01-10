package com.noomit.radioalarm02.ui.radio_browser.stationlist

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.OvershootInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.data.StationModel
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationListAdapter
import com.noomit.radioalarm02.ui.radio_browser.stationlist.views.NowPlayingView
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout

interface IStationListLayout {
    val playerControll: PlayerControlView
    val playerView: PlayerView
    var delegate: StationListViewListener?

    fun setStationsAdapter(adapter: StationListAdapter)
    fun showLoading()
    fun showContent(values: List<StationModel>)
    fun nowPlaying(station: StationModel, inFavorites: Boolean)
}

interface StationListViewListener {
    fun onFavoriteClick()
}

class StationListLayout(context: Context, attributeSet: AttributeSet? = null) :
    ContourLayout(context, attributeSet), IStationListLayout {

    override var delegate: StationListViewListener? = null

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

    private val stationsCount = TextView(
        context,
        null,
        appTheme.nowPlaying.stationCount.attr
    ).apply {
        isVisible = false
    }

    private val nowPlayingView = NowPlayingView(context).apply {
        btnFav.setOnClickListener { this@StationListLayout.delegate?.onFavoriteClick() }
    }

    private val loadingIndicator = ProgressBar(context)

    init {
        contourHeightMatchParent()

        val matchParentWidth = matchParentX(marginLeft = 16.dip, marginRight = 16.dip)

        playerControll.layoutBy(
            rightTo { parent.right() },
            bottomTo { parent.bottom() }
        )

        stationsCount.layoutBy(
            rightTo { parent.right() - 16.xdip },
            topTo { parent.top() }
        )

        rvStationList.layoutBy(
            matchParentWidth,
            bottomTo { playerControll.top() }.topTo { stationsCount.bottom() }
        )

        loadingIndicator.layoutBy(
            centerHorizontallyTo { parent.centerX() },
            centerVerticallyTo { parent.centerY() },
        )

        val xPadding = { if (!nowPlayingView.isSelected) 0.xdip else 32.xdip }
        val yPadding = { if (!nowPlayingView.isSelected) 0.ydip else 64.ydip }

        nowPlayingView.layoutBy(
            x = leftTo { parent.left() + xPadding() }
                .rightTo {
                    if (!nowPlayingView.isSelected) {
                        playerControll.left() + xPadding()
                    } else {
                        parent.right() - xPadding()
                    }
                },
            y = topTo {
                if (!nowPlayingView.isSelected) {
                    rvStationList.bottom()
                } else {
                    parent.top() + yPadding()
                }
            }.bottomTo { parent.bottom() - yPadding() }
        )

        nowPlayingView.setOnClickListener {
            TransitionManager.beginDelayedTransition(this, ChangeBounds()
                .setInterpolator(OvershootInterpolator(1f))
                .setDuration(400)
            )
            nowPlayingView.isSelected = !nowPlayingView.isSelected
            requestLayout()
        }
    }

    override fun setStationsAdapter(adapter: StationListAdapter) {
        rvStationList.adapter = adapter
    }

    override fun showLoading() {
        loadingIndicator.isVisible = true
        rvStationList.isVisible = false
    }

    override fun showContent(values: List<StationModel>) {
        (rvStationList.adapter as StationListAdapter).submitList(values)
        loadingIndicator.isVisible = false
        rvStationList.isVisible = true
        stationsCount.text = context.getString(R.string.station_count, values.size)
        stationsCount.isVisible = true
    }

    override fun nowPlaying(station: StationModel, inFavorites: Boolean) {
        nowPlayingView.update(station, inFavorites)
    }
}
