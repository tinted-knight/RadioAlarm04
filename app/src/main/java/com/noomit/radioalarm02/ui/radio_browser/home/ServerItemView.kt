package com.noomit.radioalarm02.ui.radio_browser.home

import android.content.Context
import android.text.TextUtils
import android.widget.TextView
import com.noomit.radioalarm02.ui.animations.ItemListAnimator
import com.squareup.contour.ContourLayout

interface IServerItem {
  fun setName(value: String)
}

class ServerItemView(context: Context) : ContourLayout(context), IServerItem {
  private val name = TextView(context).apply {
    isSingleLine = true
    ellipsize = TextUtils.TruncateAt.MARQUEE
  }

  init {
    contourHeightOf {
      name.height() + 16.ydip
    }

    stateListAnimator = ItemListAnimator(this)

    name.layoutBy(
      matchParentX(16, 16),
      topTo { parent.top() + 8.ydip }
    )
  }

  override fun setName(value: String) {
    name.text = value
  }
}
