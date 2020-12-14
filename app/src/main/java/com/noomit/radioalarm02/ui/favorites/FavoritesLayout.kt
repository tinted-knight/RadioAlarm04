package com.noomit.radioalarm02.ui.favorites

import android.content.Context
import android.view.LayoutInflater
import android.view.animation.OvershootInterpolator
import android.widget.ProgressBar
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
import com.squareup.contour.ContourLayout

interface IFavoritesLayout {
    val playerControll: PlayerControlView
    val playerView: PlayerView
    var delegate: FavoritesViewListener?

    fun setStationsAdapter(adapter: StationListAdapter)
    fun showLoading()
    fun showContent(values: List<StationModel>)
    fun nowPlaying(station: StationModel, inFavorites: Boolean)
    fun nowPlayingEmpty()
}

interface FavoritesViewListener {
    fun onFavoriteClick()
}

class FavoritesLayout(context: Context) : ContourLayout(context), IFavoritesLayout {

    override var delegate: FavoritesViewListener? = null

    private val inflater = LayoutInflater.from(context)

    override val playerControll =
        inflater.inflate(R.layout.exo_player_control_view, null) as PlayerControlView

    override val playerView = PlayerView(context).apply {
        useController = false
        isVisible = false
    }

    private val stationList = RecyclerView(context).apply {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        isVerticalScrollBarEnabled = true
    }

    private val nowPlayingView = NowPlayingView(context).apply {
        btnFav.setOnLongClickListener {
            this@FavoritesLayout.delegate?.onFavoriteClick()
            true
        }
    }

    private val loadingIndicator = ProgressBar(context)

    init {
        contourHeightMatchParent()

        val matchParentWidth = matchParentX(marginLeft = 16.dip, marginRight = 16.dip)

        playerControll.layoutBy(
            rightTo { parent.right() },
            bottomTo { parent.bottom() }
        )

        stationList.layoutBy(
            matchParentWidth,
            bottomTo { playerControll.top() }.topTo { parent.top() }
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
                    stationList.bottom()
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
        stationList.adapter = adapter
    }

    override fun showLoading() {
        loadingIndicator.isVisible = true
        stationList.isVisible = false
    }

    override fun showContent(values: List<StationModel>) {
        (stationList.adapter as StationListAdapter).submitList(values)
        loadingIndicator.isVisible = false
        stationList.isVisible = true
    }

    override fun nowPlaying(station: StationModel, inFavorites: Boolean) {
        nowPlayingView.update(station, inFavorites)
    }

    override fun nowPlayingEmpty() {
        nowPlayingView.updateEmpty()
    }
}