package com.noomit.radioalarm02.ui.alarm_list

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import com.google.android.material.textview.MaterialTextView
import com.noomit.domain.entities.AlarmModel
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.ui.alarm_list.adapters.AlarmListAdapter
import com.noomit.radioalarm02.ui.alarm_list.adapters.MarginItemDecoration
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
    fun showContent(values: List<AlarmModel>)
    fun showEmpty()
}

// #todo bbar as separate layout
class HomeLayout(context: Context, attrSet: AttributeSet? = null) : ContourLayout(context),
    IHomeLayout {

    override var delegate: IHomeLayoutDelegate? = null

    private val recycler = RecyclerView(context).apply {
        layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        setHasFixedSize(true)
        isVerticalScrollBarEnabled = true
        addItemDecoration(MarginItemDecoration(R.dimen.recyclerAlarmVertical))
        addOnScrollListener(recyclerScrollListener)
    }

    private val btnFavorites = MaterialTextView(
        ContextThemeWrapper(context, appTheme.btns.bbarFav.style),
        null,
        appTheme.btns.bbarFav.attr
    ).apply {
        text = context.getString(R.string.favorites)
        stateListAnimator = ItemListAnimator(this)
        background = GradientDrawable()
        setOnClickListener { delegate?.onFavoriteClick() }
    }

    private val btnBrowse = MaterialTextView(
        ContextThemeWrapper(context, appTheme.btns.bbarBrowse.style),
        null,
        appTheme.btns.bbarBrowse.attr
    ).apply {
        text = context.getString(R.string.browse_radio)
        stateListAnimator = ItemListAnimator(this)
        background = GradientDrawable()
        setOnClickListener { delegate?.onBrowseClick() }
    }

    private val btnAddAlarm = MaterialTextView(
        ContextThemeWrapper(context, appTheme.btns.bbarAddAlarm.style),
        null,
        appTheme.btns.bbarAddAlarm.attr
    ).apply {
        text = context.getString(R.string.add_alarm)
        stateListAnimator = ItemListAnimator(this)
        setOnClickListener { delegate?.onAddAlarmClick() }
    }

    private val helpView = HelpView(context).apply {
        setOnClickListener(::helpClick)
    }

    private val isHelpExpanded: Boolean get() = helpView.isSelected

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

        val densityDpi = resources.configuration.densityDpi
        val fabSize = if (densityDpi < 460f) appTheme.helpView.fabSizeSmall else appTheme.helpView.fabSize
        helpView.layoutBy(
            rightTo { if (isHelpExpanded) parent.right() - 16.xdip else parent.right() - 16.xdip }
                .widthOf { if (isHelpExpanded) parent.width() * 3 / 4 else fabSize.xdip },
            bottomTo { btnBrowse.top() - 16.ydip }
                .heightOf { if (isHelpExpanded) parent.height() * 3 / 4 else fabSize.ydip }
        )
    }

    override fun setAdapter(adapter: AlarmListAdapter) {
        recycler.adapter = adapter
    }

    override fun showContent(values: List<AlarmModel>) {
        (recycler.adapter as AlarmListAdapter).submitList(values)
        recycler.isVisible = true
    }

    override fun showEmpty() {
        // #todo empty view
        recycler.isVisible = false
    }

    private fun helpClick(view: View) {
        TransitionManager.beginDelayedTransition(this,
            ChangeBounds().apply {
                duration = 300L
                interpolator = if (view.isSelected) FastOutSlowInInterpolator() else OvershootInterpolator(1f)
            }
        )
        view.isSelected = !view.isSelected
        requestLayout()
    }

    private val recyclerScrollListener: RecyclerView.OnScrollListener
        get() = object : RecyclerView.OnScrollListener() {

            private var isFabVisible = true

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && isFabVisible) {
                    isFabVisible = false
                    if (helpView.isSelected) helpClick(helpView)
                    AnimatorSet().apply {
                        duration = 200L
                        playTogether(
                            ObjectAnimator.ofFloat(helpView, View.SCALE_X, 1f, 0f),
                            ObjectAnimator.ofFloat(helpView, View.SCALE_Y, 1f, 0f)
                        )
                        doOnEnd {
                            helpView.isClickable = false
                        }
                    }.start()
                } else if (dy < 0 && !isFabVisible) {
                    isFabVisible = true
                    helpView.isClickable = true
                    helpView.animate()
                        .scaleX(1f).scaleY(1f).start()
                }
            }
        }
}
