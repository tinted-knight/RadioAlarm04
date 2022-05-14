package com.noomit.radioalarm02.ui.animations

import android.animation.*
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.animation.LinearInterpolator
import com.noomit.radioalarm02.util.withAlpha

class BBarAnimator(private val view: View, private val strokeColor: Int) :
  StateListAnimator() {

  private val animationDuration = 30L
  private val animationInterpolator = LinearInterpolator()

  init {
    addState(
      intArrayOf(android.R.attr.state_pressed),
      pressAnimator(),
    )
    addState(
      intArrayOf(-android.R.attr.state_pressed),
      releaseAnimator(),
    )
  }

  private fun pressAnimator() = createAnimator(
    toScale = 0.95f,
    toAlpha = 0.5f,
    toColor = strokeColor withAlpha 100,
    toRadius = 4.0f
  )

  private fun releaseAnimator() = createAnimator(
    toScale = 1.0f,
    toAlpha = 1.0f,
    toColor = strokeColor withAlpha 0,
    toRadius = 0.1f
  )

  private fun createAnimator(
    toScale: Float,
    toAlpha: Float,
    toColor: Int,
    toRadius: Float,
  ): Animator {
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

    val borderAnim = ObjectAnimator.ofArgb(toColor).apply {
      addUpdateListener { (view.background as GradientDrawable).setStroke(2, it.animatedValue as Int) }
    }

    val cornerRadiusAnim = ObjectAnimator.ofFloat(toRadius).apply {
      addUpdateListener { (view.background as GradientDrawable).cornerRadius = (it.animatedValue as Float) }
    }

    return AnimatorSet().apply {
      playTogether(scaleAnim, alphaAnim, borderAnim, cornerRadiusAnim)
    }
  }
}
