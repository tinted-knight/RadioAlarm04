package com.noomit.radioalarm02.ui.animations

import android.animation.*
import android.view.View
import android.view.animation.LinearInterpolator

class ItemListAnimator(private val view: View) : StateListAnimator() {

  private val animationDuration = 60L
  private val animationInterpolator = LinearInterpolator()

  init {
    addState(
      intArrayOf(android.R.attr.state_pressed),
      createAnimator(toScale = 0.95f, toAlpha = 0.5f)
    )
    addState(
      intArrayOf(-android.R.attr.state_pressed),
      createAnimator(toScale = 1f, toAlpha = 1.0f)
    )
  }

  private fun createAnimator(toScale: Float, toAlpha: Float): Animator {
    val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, toScale)
    val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, toScale)

    val scaleAnim = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
      duration = animationDuration
      interpolator = animationInterpolator
    }

    val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, toAlpha)
    val alphaAnim = ObjectAnimator.ofPropertyValuesHolder(view, alpha).apply {
      duration = animationDuration
      interpolator = animationInterpolator
    }

    return AnimatorSet().apply {
      playTogether(scaleAnim, alphaAnim)
    }
  }
}
