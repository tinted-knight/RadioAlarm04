package com.noomit.radioalarm02.ui.alarm_list.select_melody

import android.content.Context
import android.os.Parcelable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import com.google.android.material.button.MaterialButton
import com.noomit.domain.entities.StationModel
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.ui.radio_browser.stationlist.IStationListLayout
import com.noomit.radioalarm02.ui.radio_browser.stationlist.StationListLayout
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationListAdapter
import com.noomit.radioalarm02.ui.radio_browser.stationlist.views.NowPlayingListener
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout

interface ISelectMelodyLayout : IStationListLayout {
    var onSetMelodyClick: (() -> Unit)?
    var onSetDefaultRingtone: (() -> Unit)?
}

class SelectMelodyLayout(context: Context, attrSet: AttributeSet? = null) :
    ContourLayout(context, attrSet), ISelectMelodyLayout {
    override var onSetMelodyClick: (() -> Unit)? = null

    override var onSetDefaultRingtone: (() -> Unit)? = null

    private val favorites = StationListLayout(context)

    private val btnSetup = MaterialButton(context).apply {
        text = resources.getString(R.string.btn_set)
        isEnabled = false
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.MARQUEE
        setOnClickListener { onSetMelodyClick?.invoke() }
    }

    private val btnSystem = MaterialButton(
        ContextThemeWrapper(context, appTheme.btns.text.style),
        null,
        appTheme.btns.text.attr,
    ).apply {
        text = resources.getString(R.string.btn_set_default)
        setOnClickListener { onSetDefaultRingtone?.invoke() }
    }

    init {
        btnSystem.layoutBy(
            leftTo { parent.left() + 8.xdip },
            bottomTo { parent.bottom() }
        )
        btnSetup.layoutBy(
            leftTo { btnSystem.right() + 8.xdip }.rightTo { parent.right() - 8.xdip },
            bottomTo { parent.bottom() }.topTo { btnSystem.top() }
        )
        favorites.layoutBy(
            matchParentX(8, 8),
            topTo { parent.top() }.bottomTo { btnSystem.top() }
        )
    }

    override val playerControl = favorites.playerControl

    override var listener: NowPlayingListener? = null

    override fun setStationsAdapter(adapter: StationListAdapter) =
        favorites.setStationsAdapter(adapter)

    override fun showLoading() = favorites.showLoading()

    override fun showContent(values: List<StationModel>) = favorites.showContent(values)

    override fun nowPlaying(station: StationModel, inFavorites: Boolean) {
        btnSetup.isEnabled = true
        favorites.nowPlaying(station, inFavorites)
    }

    override fun nowPlayingEmpty() {
        btnSetup.isEnabled = false
        favorites.nowPlayingEmpty()
    }

    override fun getRecyclerState(): Parcelable? = null

    override fun setRecyclerState(state: Parcelable) {}
}
