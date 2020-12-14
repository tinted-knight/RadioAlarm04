package com.noomit.radioalarm02.ui.radio_browser.home

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.PaintDrawable
import android.view.KeyEvent
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.noomit.radioalarm02.ui.animations.PushOnPressAnimator
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout

class ServerListView(context: Context) : ContourLayout(context) {

    val recycler = RecyclerView(context).apply {
        setHasFixedSize(true)
        layoutManager = LinearLayoutManager(context)
        isVerticalScrollBarEnabled = true
    }

    val active = TextView(context).apply {
        text = "Looking for available server..."
    }

    init {
        contourHeightWrapContent()
        background = PaintDrawable(appTheme.nowPlaying.bgColor)
        stateListAnimator = PushOnPressAnimator(this)
        registerBackpressListener()

        collapsedLayout()
    }

    private fun collapsedLayout() {
        toggleCornerRaduis(false)
        active.layoutBy(
            x = matchParentX(marginLeft = 16, marginRight = 16),
            y = topTo { parent.top() }
        )
        recycler.layoutBy(emptyX(), emptyY())

        recycler.isVisible = false
        active.isVisible = true
    }

    private fun expandedLayout() {
        toggleCornerRaduis(true)
        recycler.isVisible = true
        active.isVisible = false
        elevation = 6.0f

        recycler.updateLayoutBy(
            x = matchParentX(marginLeft = 8, marginRight = 8),
            y = topTo { parent.top() }
        )
    }

    override fun setSelected(selected: Boolean) {
        if (isLaidOut && selected == this.isSelected) return
        super.setSelected(selected)
        if (!selected) collapsedLayout() else expandedLayout()
    }

    override fun getBackground() = super.getBackground() as PaintDrawable

    private fun toggleCornerRaduis(show: Boolean) {
        val fromRadius = if (show) 0.01f else 12.0f.dip
        val toRadius = if (show) 12.0f.dip else 0.01f

        if (isLaidOut) {
            ObjectAnimator.ofFloat(fromRadius, toRadius)
                .apply { addUpdateListener { background.setCornerRadius(it.animatedValue as Float) } }
                .setDuration(200)
                .start()
        } else {
            background.setCornerRadius(toRadius)
        }
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
}