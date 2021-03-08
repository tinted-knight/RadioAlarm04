package com.noomit.radioalarm02.ui.animations

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.drawable.PaintDrawable
import android.view.ViewGroup
import androidx.transition.Transition
import androidx.transition.TransitionValues

@Suppress("PrivatePropertyName")
class TitleTransition(
    private val collapse: Boolean,
) : Transition() {

    private val PROPNAME_BACKGROUND = "com.noomit.radioalarm02.ui.animations:TitleTransition:background"

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        transitionValues.values[PROPNAME_BACKGROUND] = view.background
    }

    override fun createAnimator(
        sceneRoot: ViewGroup,
        startValues: TransitionValues?,
        endValues: TransitionValues?,
    ): Animator? {
        if (startValues == null || endValues == null) return null

        val fromAlpha = if (collapse) 255 else 0
        val toAlpha = if (collapse) 0 else 255

        val fromRadius = if (collapse) 16.0f else 0.1f
        val toRadius = if (collapse) 0.1f else 16.0f

        val view = endValues.view

        val alphaAnimator = ObjectAnimator.ofInt(fromAlpha, toAlpha)
        alphaAnimator.addUpdateListener {
            view.background.alpha = it.animatedValue as Int
        }

        val cornerAnimator = ObjectAnimator.ofFloat(fromRadius, toRadius)
        cornerAnimator.addUpdateListener {
            (view.background as PaintDrawable).setCornerRadius(it.animatedValue as Float)
        }

        return AnimatorSet().apply {
            playTogether(alphaAnimator, cornerAnimator)
        }
    }
}
