package com.noomit.radioalarm02.ui.alarm_list

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.PaintDrawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageButton
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionSet
import com.google.android.material.textview.MaterialTextView
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.ui.animations.ElevationAnimator
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout

class HelpView(context: Context, attrs: AttributeSet? = null) : ContourLayout(context, attrs) {

    private val browseHelp = materialTextView(R.string.help_browse)

    private val deleteHelp = materialTextView(R.string.help_delete)

    private val melodyHelp = materialTextView(R.string.help_melody)

    private val browseIcon = imageView(appTheme.helpView.browseIcon)

    private val deleteIcon = imageView(appTheme.helpView.deleteIcon)

    private val melodyIcon = imageView(appTheme.helpView.melodyIcon)

    private val browseDivider = divider()
    private val deleteDivider = divider()

    private val fab = ImageButton(context, null, appTheme.helpView.fabStyleId)

    private val fabCornerRadius = (appTheme.helpView.fabSize.toFloat() / 2).dip
    private val cardCornerRadius = 8.0f.dip

    private val animationDuration = 300L

    init {
        background = PaintDrawable(ContextCompat.getColor(context, appTheme.helpView.bgColor))
        elevation = 6.0f
        stateListAnimator = ElevationAnimator(this)
        registerBackpressListener()

        collapseLayout()
    }

    fun expand() {
        if (!isSelected) performClick()
    }

    fun collapse() {
        if (isSelected) performClick()
    }

    val layoutTransition: Transition
        get() {
            return if (!isSelected) {
                // expanding
                ChangeBounds().apply {
                    duration = animationDuration
                    interpolator = OvershootInterpolator(1f)
                }
            } else {
                // collapsing
                TransitionSet()
                    .addTransition(ChangeBounds().apply {
                        duration = animationDuration
                        interpolator = FastOutSlowInInterpolator()
                    })
                    .addTransition(Fade().apply {
                        addTarget(fab)
                        startDelay = animationDuration / 4
                        duration = animationDuration - startDelay
                    })
            }
        }

    override fun setSelected(selected: Boolean) {
        if (isLaidOut && selected == this.isSelected) return
        super.setSelected(selected)
        if (!selected) collapseLayout() else expandLayout()
    }

    private fun collapseLayout() {
        animateLayout(expand = false)
        contourHeightOf { appTheme.helpView.fabSize.ydip }
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
        contourHeightWrapContent()
        animateLayout(expand = true)
        browseHelp.isVisible = true
        browseIcon.isVisible = true
        browseDivider.isVisible = true
        deleteHelp.isVisible = true
        deleteIcon.isVisible = true
        deleteDivider.isVisible = true
        melodyHelp.isVisible = true
        melodyIcon.isVisible = true
        fab.isVisible = false

        setPadding(8.dip, 8.dip, 8.dip, 8.dip)

        val hSpacing = 16.xdip
        val vSpacing = 16.ydip

        val vPaddingIcon = 4.ydip

        browseIcon.updateLayoutBy(
            x = leftTo { parent.left() },
            y = topTo { browseHelp.top() + vPaddingIcon }
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
            y = topTo { deleteHelp.top() + vPaddingIcon }
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
            y = topTo { melodyHelp.top() + vPaddingIcon }
        )
        melodyHelp.updateLayoutBy(
            x = leftTo { melodyIcon.right() + hSpacing }.rightTo { parent.right() },
            y = topTo { deleteDivider.bottom() + vSpacing }
        )
    }

    override fun getBackground() = super.getBackground() as PaintDrawable

    private fun animateLayout(expand: Boolean) {
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
                duration = animationDuration
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

    private fun imageView(@DrawableRes resId: Int) =
        ImageView(context, null, appTheme.helpView.iconAttr).apply {
            setImageDrawable(ContextCompat.getDrawable(context, resId))
        }

    private fun materialTextView(@StringRes resId: Int) =
        MaterialTextView(context, null, appTheme.helpView.text).apply {
            text = context.getString(resId)
        }

    private fun divider() = View(context, null, appTheme.helpView.dividerAttr)
}
