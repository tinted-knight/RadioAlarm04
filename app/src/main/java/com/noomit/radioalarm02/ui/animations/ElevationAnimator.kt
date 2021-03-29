package com.noomit.radioalarm02.ui.animations

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.StateListAnimator
import android.view.View
import android.view.animation.LinearInterpolator

class ElevationAnimator(private val view: View) : StateListAnimator() {

    private val animationDuration = 60L
    private val animationInterpolator = LinearInterpolator()

    init {
        addState(
            intArrayOf(android.R.attr.state_pressed),
            pressAnimator()
        )
        addState(
            intArrayOf(-android.R.attr.state_pressed),
            releaseAnimator()
        )
    }

    private fun pressAnimator() = createAnimator(6.0f, 12.0f)

    private fun releaseAnimator() = createAnimator(12.0f, 6.0f)

    private fun createAnimator(fromElevation: Float, toElevation: Float): Animator {
        return ObjectAnimator.ofFloat(fromElevation, toElevation).apply {
            duration = animationDuration
            interpolator = animationInterpolator
            addUpdateListener { view.elevation = it.animatedValue as Float }
        }
    }
}
