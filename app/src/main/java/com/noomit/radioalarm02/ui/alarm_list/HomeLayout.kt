package com.noomit.radioalarm02.ui.alarm_list

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
class HomeLayout(context: Context, attrs: AttributeSet? = null) : ContourLayout(context, attrs),
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

    private val emptyImage = ImageView(context).apply {
        setImageDrawable(ContextCompat.getDrawable(context, R.drawable.alarm_sad))
        isVisible = false
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

        val fabSize = appTheme.helpView.fabSize
        helpView.layoutBy(
            rightTo { if (isHelpExpanded) parent.right() - 16.xdip else parent.right() - 16.xdip }
                .widthOf { if (isHelpExpanded) parent.width() * 3 / 4 else fabSize.xdip },
            bottomTo { btnBrowse.top() - 16.ydip }
//                .heightOf { if (isHelpExpanded) parent.height() * 3 / 4 else fabSize.ydip }
        )
        emptyImage.layoutBy(
            leftTo { parent.left() + parent.width() / 4 }
                .rightTo { parent.width() - parent.width() / 4 },
            topTo { parent.top() + (parent.height() - btnAddAlarm.height()) / 4 }
                .heightOf { emptyImage.width().toY() }
        )
    }

    override fun setAdapter(adapter: AlarmListAdapter) {
        recycler.adapter = adapter
    }

    override fun showContent(values: List<AlarmModel>) {
        (recycler.adapter as AlarmListAdapter).submitList(values)
        recycler.isVisible = true
        emptyImage.isVisible = false
        helpView.collapse()
    }

    override fun showEmpty() {
        // #todo empty view
        recycler.isVisible = false
        emptyImage.apply {
            alpha = 0f
            isVisible = true
            helpView.expand()
            animate().setDuration(200L)
                .alpha(0.85f)
                .setInterpolator(LinearInterpolator())
                .setListener(null)
            //#achtung: May helpView leake in Runnable???
//                .setListener(AnimatorListener(
//                    onEnd = {
//                        Handler(Looper.getMainLooper())
//                            .postDelayed({
//                                helpView.expand()
//                            }, 150)
//                    }
//                ))
        }
    }

    private fun helpClick(view: View) {
        TransitionManager.beginDelayedTransition(this, helpView.layoutTransition)
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
