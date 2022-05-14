package com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.TextView
import com.noomit.radioalarm02.ui.animations.PushOnPressAnimator
import com.squareup.contour.ContourLayout

interface IStationItem {
  fun setName(value: String)
  fun setCount(value: String)
}

class StationItemView(
  context: Context,
  attrSet: AttributeSet? = null,
) : ContourLayout(context, attrSet), IStationItem {

  private val stationName = TextView(context).apply {
//        text = "Stub station"
    isSingleLine = true
    ellipsize = TextUtils.TruncateAt.END
  }

  private val stationCount = TextView(context).apply {
    text = ""
  }

  init {
    val outValue = TypedValue()
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, outValue, true)
    setBackgroundResource(outValue.resourceId)

    stateListAnimator = PushOnPressAnimator(this)

    val hPadding = 16.xdip
    val vPadding = 16.ydip

    contourWidthMatchParent()
    contourWidthMatchParent()
    contourHeightOf {
      stationName.height() + vPadding * 2
    }

    stationCount.layoutBy(
      x = rightTo { parent.right() - hPadding },
      y = centerVerticallyTo { stationName.centerY() }
    )

    stationName.layoutBy(
      x = leftTo { parent.left() + hPadding }
        .rightTo { stationCount.left() - hPadding },
      y = topTo { parent.top() + vPadding },
    )
  }

  override fun setName(value: String) {
    stationName.text = value
  }

  override fun setCount(value: String) {
    stationCount.text = value
  }
}
