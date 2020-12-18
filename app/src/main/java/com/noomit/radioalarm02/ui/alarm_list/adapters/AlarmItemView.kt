package com.noomit.radioalarm02.ui.alarm_list.adapters

import android.content.Context
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout
import kotlin.contracts.ExperimentalContracts

@ExperimentalContracts
class AlarmItemView(context: Context, attrSet: AttributeSet? = null) :
    ContourLayout(context, attrSet) {

    private val time = TextView(context).apply {
        text = "08:00"
    }

    private val switch = SwitchMaterial(context)

    private val melody = TextView(context).apply {
        text = "Long title of radio station"
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
    }

    private val btnDelete = ImageButton(
        ContextThemeWrapper(context, appTheme.alarmItem.favoriteStyleId),
        null,
        appTheme.alarmItem.favoriteStyleId,
    ).apply {
        setImageResource(appTheme.alarmItem.iconFavorite)
    }

    private fun dayOfWeek() = TextView(
        ContextThemeWrapper(context, appTheme.alarmItem.dayOfWeekStyle),
        null,
        appTheme.alarmItem.dayOfWeekStyle,
    ).apply {
        layoutParams = LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
            1.0f
        )
    }

    private val monday = dayOfWeek().apply {
        text = "mon"
    }
    private val tuesday = dayOfWeek().apply {
        text = "tue"
    }
    private val wednesday = dayOfWeek().apply {
        text = "wed"
    }
    private val thursday = dayOfWeek().apply {
        text = "thu"
    }
    private val friday = dayOfWeek().apply {
        text = "fri"
    }
    private val saturday = dayOfWeek().apply {
        text = "sat"
    }
    private val sunday = dayOfWeek().apply {
        text = "sun"
    }

    private val week = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        addView(monday)
        addView(tuesday)
        addView(wednesday)
        addView(thursday)
        addView(friday)
        addView(saturday)
        addView(sunday)
    }

    init {
        val x = 8
        val y = 8

        val xPadding = x.xdip
        val yPadding = y.ydip

        switch.layoutBy(
            rightTo { parent.right() - xPadding },
            topTo { parent.top() + yPadding }
        )
        time.layoutBy(
            leftTo { parent.left() + xPadding }.rightTo { switch.left() - xPadding },
            topTo { parent.top() + yPadding }
        )
        btnDelete.layoutBy(
            rightTo { parent.right() - xPadding },
            topTo { switch.bottom() + yPadding }
        )
        melody.layoutBy(
            leftTo { parent.left() + xPadding }.rightTo { btnDelete.left() - xPadding },
            centerVerticallyTo { btnDelete.centerY() }
        )
        week.layoutBy(
            matchParentX(x, y),
            topTo { btnDelete.bottom() + yPadding }
        )
    }
}
