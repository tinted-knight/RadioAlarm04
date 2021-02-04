package com.noomit.radioalarm02.ui.alarm_list

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.PaintDrawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.textview.MaterialTextView
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.ui.animations.ElevationAnimator
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout

class HelpView(context: Context, attrs: AttributeSet? = null) : ContourLayout(context) {

    private val browseHelp = MaterialTextView(context, null, appTheme.helpView.text).apply {
        text = context.getString(R.string.help_browse)
    }

    private val deleteHelp = MaterialTextView(context, null, appTheme.helpView.text).apply {
        text = context.getString(R.string.help_delete)
    }

    private val melodyHelp = MaterialTextView(context, null, appTheme.helpView.text).apply {
        text = context.getString(R.string.help_melody)
    }

    private val browseIcon = addImageView().apply {
//        imageTintList = ColorStateList.valueOf(Color.WHITE)
        setImageDrawable(ContextCompat.getDrawable(context, appTheme.helpView.browseIcon))
    }

    private val deleteIcon = addImageView().apply {
        setImageDrawable(ContextCompat.getDrawable(context, appTheme.helpView.deleteIcon))
    }

    private val melodyIcon = addImageView().apply {
        setImageDrawable(ContextCompat.getDrawable(context, appTheme.helpView.melodyIcon))
    }

    private val browseDivider = addDivider()
    private val deleteDivider = addDivider()

    private val fab = ImageButton(context, null, appTheme.helpView.fabStyleId)

    private val fabCornerRadius = (appTheme.helpView.fabSize.toFloat() / 2).dip
    private val cardCornerRadius = 8.0f.dip

    init {
        background = PaintDrawable(ContextCompat.getColor(context, appTheme.helpView.bgColor))
        elevation = 4.0f
        stateListAnimator = ElevationAnimator(this)
        registerBackpressListener()

        collapseLayout()
    }

    override fun setSelected(selected: Boolean) {
        if (isLaidOut && selected == this.isSelected) return
        super.setSelected(selected)
        if (!selected) collapseLayout() else expandLayout()
    }

    private fun collapseLayout() {
        toggleCornerRaduis(expand = false)
        contourHeightMatchParent()
        setPadding(0.dip, 0.dip, 0.dip, 0.dip)

        browseHelp.isVisible = false
        browseIcon.isVisible = false
        browseDivider.isVisible = false
        deleteHelp.isVisible = false
        deleteIcon.isVisible = false
        deleteDivider.isVisible = false
        melodyHelp.isVisible = false
        melodyIcon.isVisible = false
        fab.isVisible = true

        fab.layoutBy(
            x = matchParentX(),
            y = matchParentY()
        )

        browseHelp.layoutBy(emptyX(), emptyY())
        browseIcon.layoutBy(emptyX(), emptyY())
        browseDivider.layoutBy(emptyX(), emptyY())
        deleteHelp.layoutBy(emptyX(), emptyY())
        deleteIcon.layoutBy(emptyX(), emptyY())
        deleteDivider.layoutBy(emptyX(), emptyY())
        melodyHelp.layoutBy(emptyX(), emptyY())
        melodyIcon.layoutBy(emptyX(), emptyY())
    }

    private fun expandLayout() {
        toggleCornerRaduis(expand = true)
        browseHelp.isVisible = true
        browseIcon.isVisible = true
        browseDivider.isVisible = true
        deleteHelp.isVisible = true
        deleteIcon.isVisible = true
        deleteDivider.isVisible = true
        melodyHelp.isVisible = true
        melodyIcon.isVisible = true
        fab.isVisible = false

        contourHeightWrapContent()
        setPadding(8.dip, 8.dip, 8.dip, 8.dip)

        val hSpacing = 16.xdip
        val vSpacing = 16.ydip

        browseIcon.updateLayoutBy(
            x = leftTo { parent.left() },
            y = centerVerticallyTo { browseHelp.centerY() }
        )
        browseHelp.updateLayoutBy(
            x = leftTo { browseIcon.right() + hSpacing }.rightTo { parent.right() },
            y = topTo { parent.top() }
        )
        browseDivider.updateLayoutBy(
            x = matchParentX(16, 16),
            y = topTo { browseHelp.bottom() + vSpacing }.heightOf { 1.ydip }
        )
        deleteIcon.updateLayoutBy(
            x = leftTo { parent.left() },
            y = centerVerticallyTo { deleteHelp.centerY() }
        )
        deleteHelp.updateLayoutBy(
            x = leftTo { deleteIcon.right() + hSpacing }.rightTo { parent.right() },
            y = topTo { browseDivider.bottom() + vSpacing }
        )
        deleteDivider.updateLayoutBy(
            x = matchParentX(16, 16),
            y = topTo { deleteHelp.bottom() + vSpacing }.heightOf { 1.ydip }
        )
        melodyIcon.updateLayoutBy(
            x = leftTo { parent.left() },
            y = centerVerticallyTo { melodyHelp.centerY() }
        )
        melodyHelp.updateLayoutBy(
            x = leftTo { melodyIcon.right() + hSpacing }.rightTo { parent.right() },
            y = topTo { deleteDivider.bottom() + vSpacing }
        )
    }

    override fun getBackground() = super.getBackground() as PaintDrawable

    private fun toggleCornerRaduis(expand: Boolean) {
        val fromRadius = if (expand) fabCornerRadius else cardCornerRadius
        val toRadius = if (expand) cardCornerRadius else fabCornerRadius

        if (isLaidOut) {
            val cornerAnimator = ObjectAnimator.ofFloat(fromRadius, toRadius).apply {
                addUpdateListener {
                    background.setCornerRadii(floatArrayOf(
                        it.animatedValue as Float, it.animatedValue as Float,
                        it.animatedValue as Float, it.animatedValue as Float,
                        0f, 0f,
                        it.animatedValue as Float, it.animatedValue as Float,
                    ))
                }
                duration = 200L
                interpolator = LinearInterpolator()
            }
            cornerAnimator.start()
        } else {
            background.setCornerRadii(floatArrayOf(
                toRadius, toRadius,
                toRadius, toRadius,
                0f, 0f,
                toRadius, toRadius,
            ))
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

    private fun addImageView() = ImageView(context, null, appTheme.helpView.iconAttr)

    private fun addDivider() = View(context, null, appTheme.helpView.dividerAttr)
}
