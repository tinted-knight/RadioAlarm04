package com.noomit.radioalarm02.ui.radio_browser.stationlist.views

import android.content.Context
import android.graphics.drawable.PaintDrawable
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat.getColor
import com.squareup.contour.ContourLayout
import com.noomit.alarmtheme.R as Rtheme

class LabeledView(context: Context) : ContourLayout(context) {
  var label: String = ""
    set(value) {
      field = value
      labelView.text = value
      requestLayout()
    }

  var value: String = ""
    set(value) {
      field = value
      valueView.text = value
      requestLayout()
    }

  private val labelView = TextView(context)

  private val valueView = TextView(context)

  init {
    contourWidthWrapContent()
    contourHeightWrapContent()

    setPadding(8.dip, 12.dip, 8.dip, 12.dip)
    elevation = 4.0f

    background = PaintDrawable(getColor(resources, Rtheme.color.clTitleBg, null)).apply {
      setCornerRadius(16.0f)
    }

    labelView.setTextColor(getColor(resources, Rtheme.color.clNowPlayingTitleExpanded, null))
    labelView.layoutBy(
      x = leftTo { parent.left() },
      y = topTo { parent.top() }
    )

    valueView.setTextColor(getColor(resources, Rtheme.color.clNowPlayingTitleExpanded, null))
    valueView.layoutBy(
      x = leftTo { labelView.right() + 4.xdip },
      y = topTo { parent.top() }
    )
  }

}
