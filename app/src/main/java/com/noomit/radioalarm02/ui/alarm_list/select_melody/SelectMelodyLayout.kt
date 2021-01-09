package com.noomit.radioalarm02.ui.alarm_list.select_melody

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import com.google.android.material.button.MaterialButton
import com.noomit.radioalarm02.data.StationModel
import com.noomit.radioalarm02.ui.favorites.FavoritesLayout
import com.noomit.radioalarm02.ui.favorites.FavoritesViewListener
import com.noomit.radioalarm02.ui.favorites.IFavoritesLayout
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationListAdapter
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout

interface ISelectMelodyLayout : IFavoritesLayout {
    var onSetMelodyClick: (() -> Unit)?
    var onSetDefaultRingtone: (() -> Unit)?
}

class SelectMelodyLayout(context: Context, attrSet: AttributeSet? = null) :
    ContourLayout(context, attrSet), ISelectMelodyLayout {

    override var onSetMelodyClick: (() -> Unit)? = null

    override var onSetDefaultRingtone: (() -> Unit)? = null

    private val favorites = FavoritesLayout(context)

    private val btnSetup = MaterialButton(context).apply {
        text = "Set"
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
        text = "Set default"
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

    override val playerControll = favorites.playerControll

    override val playerView = favorites.playerView

    override var delegate: FavoritesViewListener? = null

    override fun setStationsAdapter(adapter: StationListAdapter) =
        favorites.setStationsAdapter(adapter)

    override fun showLoading() = favorites.showLoading()

    override fun showContent(values: List<StationModel>) = favorites.showContent(values)

    override fun nowPlaying(station: StationModel, inFavorites: Boolean) {
//        btnSetup.text = station.name
        btnSetup.isEnabled = true
        favorites.nowPlaying(station, inFavorites)
    }

    override fun nowPlayingEmpty() {
        btnSetup.isEnabled = false
        favorites.nowPlayingEmpty()
    }
}
