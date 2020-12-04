package com.noomit.radioalarm02.ui.radio_browser.stationlist.views

import android.content.Context
import android.widget.TextView
import com.squareup.contour.ContourLayout

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

        labelView.layoutBy(
            leftTo { parent.left() },
            topTo { parent.top() }
        )
        valueView.layoutBy(
            leftTo { labelView.right() + 4.xdip },
            topTo { parent.top() }
        )
    }
}
