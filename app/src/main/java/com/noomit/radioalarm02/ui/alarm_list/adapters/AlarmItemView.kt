package com.noomit.radioalarm02.ui.alarm_list.adapters

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.ui.alarm_list.adapters.IAlarmItem.Companion.days
import com.noomit.radioalarm02.ui.theme.appTheme
import com.squareup.contour.ContourLayout
import java.util.*

interface IAlarmItemActions {
    fun onDeleteClick()
    fun onDeleteLongClick()
    fun onSwitchChange(isChecked: Boolean)
    fun onTimeClick()
    fun onDayClick()
    fun onMelodyClick()
    fun onMelodyLongClick()
    fun onDayOfWeekClick(day: Int)
}

interface IAlarmItem {
    infix fun time(value: String)
    infix fun day(value: String)
    infix fun switch(isChecked: Boolean)
    infix fun melody(value: String)
    fun checkDay(day: Int, isActive: Boolean)

    var delegate: IAlarmItemActions?

    companion object {
        val days = listOf(
            Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
            Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
        )
    }
}

class AlarmItemView(context: Context, attrSet: AttributeSet? = null) :
    ContourLayout(context, attrSet), IAlarmItem {

    override var delegate: IAlarmItemActions? = null

    private val time = MaterialTextView(context).apply {
        text = "08:00"
        setOnClickListener { delegate?.onTimeClick() }
    }

    private val day = MaterialTextView(context).apply {
        text = "Dec, 42"
        setOnClickListener { delegate?.onDayClick() }
    }

    private val switch = SwitchMaterial(context).apply {
        setOnCheckedChangeListener { _, isChecked ->
            delegate?.onSwitchChange(isChecked)
        }
    }

    private val melody = MaterialTextView(context).apply {
        text = "Long title of radio station, Long title of radio station, Long title of radio"
        maxLines = 1
        ellipsize = TextUtils.TruncateAt.END
        setOnClickListener { delegate?.onMelodyClick() }
        setOnLongClickListener {
            delegate?.onMelodyLongClick()
            true
        }
    }

    private val btnDelete = ImageButton(
        ContextThemeWrapper(context, appTheme.alarmItem.favoriteStyleId),
        null,
        appTheme.alarmItem.favoriteStyleId,
    ).apply {
        setImageResource(appTheme.alarmItem.iconFavorite)
        setOnClickListener { delegate?.onDeleteClick() }
        setOnLongClickListener {
            delegate?.onDeleteLongClick()
            true
        }
    }

    private val monday = dayOfWeek().apply {
        text = "mon"
        setOnClickListener { delegate?.onDayOfWeekClick(days[0]) }
    }
    private val tuesday = dayOfWeek().apply {
        text = "tue"
        setOnClickListener { delegate?.onDayOfWeekClick(days[1]) }
    }
    private val wednesday = dayOfWeek().apply {
        text = "wed"
        setOnClickListener { delegate?.onDayOfWeekClick(days[2]) }
    }
    private val thursday = dayOfWeek().apply {
        text = "thu"
        setOnClickListener { delegate?.onDayOfWeekClick(days[3]) }
    }
    private val friday = dayOfWeek().apply {
        text = "fri"
        setOnClickListener { delegate?.onDayOfWeekClick(days[4]) }
    }
    private val saturday = dayOfWeek().apply {
        text = "sat"
        setOnClickListener { delegate?.onDayOfWeekClick(days[5]) }
    }
    private val sunday = dayOfWeek().apply {
        text = "sun"
        setOnClickListener { delegate?.onDayOfWeekClick(days[6]) }
    }

    private val dayViews: Map<Int, TextView> = mapOf(
        days[0] to monday,
        days[1] to tuesday,
        days[2] to wednesday,
        days[3] to thursday,
        days[4] to friday,
        days[5] to saturday,
        days[6] to sunday,
    )

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
        contourHeightWrapContent()

        val x = 8
        val y = 8

        val xPadding = x.xdip
        val yPadding = y.ydip

        switch.layoutBy(
            rightTo { parent.right() - xPadding },
            topTo { parent.top() + yPadding }
        )
        time.layoutBy(
            leftTo { parent.left() + xPadding },
            centerVerticallyTo { switch.centerY() }
        )
        day.layoutBy(
            leftTo { time.right() + xPadding },
            topTo { time.top() }
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

    private fun dayOfWeek() = MaterialTextView(
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

    override fun time(value: String) {
        time.text = value
    }

    override fun day(value: String) {
        day.text = value
    }

    override fun switch(isChecked: Boolean) {
        switch.isChecked = isChecked
    }

    override fun melody(value: String) {
        melody.text = value
    }

    override fun checkDay(day: Int, isActive: Boolean) {
        val textColor = when {
            isActive -> R.color.colorDayTextActive
            else -> R.color.colorDayTextInactive
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            dayViews[day]?.setTextColor(resources.getColor(textColor, null))
        } else {
            dayViews[day]?.setTextColor(resources.getColor(textColor))
        }
    }
}