package com.noomit.radioalarm02.ui.animations

import android.animation.Animator

class AnimatorListener(
  private val onStart: ((Animator?) -> Unit)? = null,
  private val onEnd: ((Animator?) -> Unit)? = null,
  private val onCancel: ((Animator?) -> Unit)? = null,
  private val onRepeat: ((Animator?) -> Unit)? = null,
) :
  Animator.AnimatorListener {

  override fun onAnimationStart(animation: Animator?) {
    onStart?.invoke(animation)
  }

  override fun onAnimationEnd(animation: Animator?) {
    onEnd?.invoke(animation)
  }

  override fun onAnimationCancel(animation: Animator?) {
    onCancel?.invoke(animation)
  }

  override fun onAnimationRepeat(animation: Animator?) {
    onRepeat?.invoke(animation)
  }
}
