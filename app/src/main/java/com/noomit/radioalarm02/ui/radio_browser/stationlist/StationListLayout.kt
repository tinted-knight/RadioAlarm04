package com.noomit.radioalarm02.ui.radio_browser.stationlist

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.animation.addListener
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.google.android.exoplayer2.ui.PlayerControlView
import com.noomit.domain.entities.StationModel
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationListAdapter
import com.noomit.radioalarm02.ui.radio_browser.stationlist.views.NowPlayingListener
import com.noomit.radioalarm02.ui.radio_browser.stationlist.views.NowPlayingView
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout

interface IStationListLayout {
    val playerControl: PlayerControlView
    var listener: NowPlayingListener?

    fun setStationsAdapter(adapter: StationListAdapter)
    fun showLoading()
    fun showContent(values: List<StationModel>)
    fun nowPlaying(station: StationModel, inFavorites: Boolean)
    fun nowPlayingEmpty()

    fun getRecyclerState(): Parcelable?
    fun setRecyclerState(state: Parcelable)
}

class StationListLayout(context: Context, attributeSet: AttributeSet? = null) :
    ContourLayout(context, attributeSet), IStationListLayout {

    override var listener: NowPlayingListener? = null
        set(value) {
            field = value
            nowPlayingView.nowPlayingListener = value
        }

    private val inflater = LayoutInflater.from(context)

    override val playerControl =
        inflater.inflate(R.layout.exo_player_control_view, null) as PlayerControlView

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
        setOnClickListener(::nowPlayingClick)
    }

    private val loadingIndicator = ProgressBar(context)

    private val dimmingView = View(context).apply {
        setBackgroundColor(ResourcesCompat.getColor(resources, appTheme.nowPlaying.dimmColor, null))
        isVisible = false
//        setOnClickListener {
//            if (nowPlayingView.isSelected) nowPlayingClick(nowPlayingView)
//        }
    }

    private val isExpanded: Boolean get() = nowPlayingView.isSelected

    init {
        contourHeightMatchParent()

        stationsCount.layoutBy(
            rightTo { parent.right() - 16.xdip },
            topTo { parent.top() }
        )

        rvStationList.layoutBy(
            matchParentX(),
            bottomTo { playerControl.top() }.topTo { stationsCount.bottom() }
        )

        loadingIndicator.layoutBy(
            centerHorizontallyTo { parent.centerX() },
            centerVerticallyTo { parent.centerY() },
        )

        dimmingView.layoutBy(
            x = matchParentX(),
            y = topTo { parent.top() }.bottomTo { playerControl.top() }
        )

        nowPlayingView.layoutBy(
            x = leftTo { parent.left() }
                .rightTo { if (expanded) parent.right() else playerControl.left() },
            y = topTo { if (expanded) parent.top() else rvStationList.bottom() }
                .bottomTo { parent.bottom() }
        )

        val fabSize = appTheme.helpView.fabSize
        playerControl.layoutBy(
            centerHorizontallyTo { if (!isExpanded) parent.right() - fabSize - 8.xdip else parent.centerX() },
            bottomTo { parent.bottom() - 8.ydip }
        )
//        playerControl.layoutBy(
//            rightTo { parent.right() - 8.xdip },
//            bottomTo { parent.bottom() - 8.ydip }
//        )
    }

    private val expanded: Boolean get() = nowPlayingView.isSelected

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

    override fun nowPlayingEmpty() {
        nowPlayingView.updateEmpty()
        dimmingView.isVisible = false
    }

    override fun getRecyclerState() = rvStationList.layoutManager?.onSaveInstanceState()

    override fun setRecyclerState(state: Parcelable) {
        rvStationList.layoutManager?.onRestoreInstanceState(state)
    }

    private fun nowPlayingClick(view: View) {
        TransitionManager.beginDelayedTransition(this, nowPlayingView.layoutTransition)

        view.isSelected = !view.isSelected

        val anim = dimmingViewAnimator(view.isSelected)
        anim.start()

        requestLayout()
    }

    private fun dimmingViewAnimator(show: Boolean): Animator {
        dimmingView.alpha = if (show) 0.0f else 0.5f
        dimmingView.isVisible = true
        val toAlpha = if (show) 0.5f else 0.0f
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, toAlpha)
        val alphaAnim = ObjectAnimator.ofPropertyValuesHolder(dimmingView, alpha).apply {
            duration = 400
            interpolator = LinearInterpolator()
            addListener(onEnd = { dimmingView.isVisible = show })
        }
        return AnimatorSet().apply { play(alphaAnim) }
    }
}
