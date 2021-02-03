package com.noomit.radioalarm02.ui.alarm_list

import android.content.Context
import android.graphics.drawable.PaintDrawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.textview.MaterialTextView
import com.noomit.radioalarm02.ui.animations.ElevationAnimator
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout

class HelpView(context: Context, attrs: AttributeSet? = null) : ContourLayout(context) {

    private val browseHelp = MaterialTextView(context, null, appTheme.helpView.text).apply {
        text = "Browse radio stations and add to Favorites to be able to set it as Alarm melody"
    }

    private val deleteHelp = MaterialTextView(context, null, appTheme.helpView.text).apply {
        text = "Hold delete icon to delete Alarm"
    }

    private val melodyHelp = MaterialTextView(context, null, appTheme.helpView.text).apply {
        text = "Tap melody field to select from Favorites, hold to test Alarm screen. While testing adjust volume"
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

    init {
        background = PaintDrawable(ContextCompat.getColor(context, appTheme.helpView.bgColor)).apply {
            setCornerRadius(48.0f)
        }
        collapsedLayout()
        elevation = 4.0f
        stateListAnimator = ElevationAnimator(this)
        registerBackpressListener()
    }

    override fun setSelected(selected: Boolean) {
        if (isLaidOut && selected == this.isSelected) return
        super.setSelected(selected)
        if (!selected) collapsedLayout() else expandedLayout()
    }

    private fun collapsedLayout() {
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
            matchParentX(),
            matchParentY()
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

    private fun expandedLayout() {
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
            leftTo { parent.left() },
            centerVerticallyTo { browseHelp.centerY() }
        )
        browseHelp.updateLayoutBy(
            leftTo { browseIcon.right() + hSpacing }.rightTo { parent.right() },
            topTo { parent.top() }
        )
        browseDivider.updateLayoutBy(
            matchParentX(16, 16),
            topTo { browseHelp.bottom() + vSpacing }.heightOf { 1.ydip }
        )
        deleteIcon.updateLayoutBy(
            leftTo { parent.left() },
            centerVerticallyTo { deleteHelp.centerY() }
        )
        deleteHelp.updateLayoutBy(
            leftTo { deleteIcon.right() + hSpacing }.rightTo { parent.right() },
            topTo { browseDivider.bottom() + vSpacing }
        )
        deleteDivider.updateLayoutBy(
            matchParentX(16, 16),
            topTo { deleteHelp.bottom() + vSpacing }.heightOf { 1.ydip }
        )
        melodyIcon.updateLayoutBy(
            leftTo { parent.left() },
            centerVerticallyTo { melodyHelp.centerY() }
        )
        melodyHelp.updateLayoutBy(
            leftTo { melodyIcon.right() + hSpacing }.rightTo { parent.right() },
            topTo { deleteDivider.bottom() + vSpacing }
        )
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
