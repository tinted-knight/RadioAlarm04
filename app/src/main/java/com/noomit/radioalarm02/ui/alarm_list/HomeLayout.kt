package com.noomit.radioalarm02.ui.alarm_list

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmListAdapter
import com.noomit.radioalarm02.ui.alarm_list.adapters.MarginItemDecoration
import com.noomit.radioalarm02.ui.animations.BBarAnimator
import com.noomit.radioalarm02.ui.animations.ItemListAnimator
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout

interface IHomeLayoutDelegate {
    fun onFavoriteClick()
    fun onAddAlarmClick()
    fun onBrowseClick()
}

interface IHomeLayout {
    var delegate: IHomeLayoutDelegate?

    fun setAdapter(adapter: AlarmListAdapter)
    fun showContent(values: List<Alarm>)
    fun showEmpty()
}

// #todo help fab
// #todo bbar as separate layout
class HomeLayout(context: Context, attrSet: AttributeSet? = null) : ContourLayout(context),
    IHomeLayout {

    override var delegate: IHomeLayoutDelegate? = null

    private val recycler = RecyclerView(context).apply {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        setHasFixedSize(true)
        isVerticalScrollBarEnabled = true
        addItemDecoration(MarginItemDecoration(R.dimen.recyclerAlarmVertical))
    }

    private val btnFavorites = MaterialTextView(
        ContextThemeWrapper(context, appTheme.btns.bbarFav.style),
        null,
        appTheme.btns.bbarFav.attr
    ).apply {
        text = context.getString(R.string.favorites)
        setOnClickListener { delegate?.onFavoriteClick() }
        stateListAnimator = ItemListAnimator(this)
    }

    private val btnBrowse = MaterialTextView(
        ContextThemeWrapper(context, appTheme.btns.bbarBrowse.style),
        null,
        appTheme.btns.bbarBrowse.attr
    ).apply {
        text = context.getString(R.string.browse_radio)

        val strokeColor = ResourcesCompat.getColor(resources, appTheme.common.clPrimary, null)
        stateListAnimator = BBarAnimator(this, strokeColor)
        background = GradientDrawable()

        setOnClickListener { delegate?.onBrowseClick() }
    }

    private val btnAddAlarm = MaterialTextView(
        ContextThemeWrapper(context, appTheme.btns.bbarAddAlarm.style),
        null,
        appTheme.btns.bbarAddAlarm.attr
    ).apply {
        text = context.getString(R.string.add_alarm)
        setOnClickListener { delegate?.onAddAlarmClick() }
        stateListAnimator = ItemListAnimator(this)
    }

    init {
        btnAddAlarm.layoutBy(
            leftTo { btnFavorites.right() }.rightTo { btnBrowse.left() },
            bottomTo { parent.bottom() }
        )
        btnFavorites.layoutBy(
            leftTo { parent.left() }.rightTo { parent.width() / 3 },
            bottomTo { parent.bottom() }
        )
        btnBrowse.layoutBy(
            rightTo { parent.right() }.leftTo { parent.width() * 2 / 3 },
            bottomTo { parent.bottom() }
        )
        recycler.layoutBy(
            matchParentX(16, 16),
            topTo { parent.top() }.bottomTo { btnAddAlarm.top() }
        )
    }

    override fun setAdapter(adapter: AlarmListAdapter) {
        recycler.adapter = adapter
    }

    override fun showContent(values: List<Alarm>) {
        (recycler.adapter as AlarmListAdapter).submitList(values)
        recycler.isVisible = true
    }

    override fun showEmpty() {
        // #todo empty view
        recycler.isVisible = false
    }
}
