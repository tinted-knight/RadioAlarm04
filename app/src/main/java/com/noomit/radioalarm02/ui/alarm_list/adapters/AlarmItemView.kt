package com.noomit.radioalarm02.ui.alarm_list.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.getResourceApi23
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

// #achtung flashes when click on day
@Suppress("DEPRECATION")
class AlarmItemView(context: Context, attrSet: AttributeSet? = null) :
    ContourLayout(context, attrSet), IAlarmItem {

    override var delegate: IAlarmItemActions? = null

    private val time = MaterialTextView(
        ContextThemeWrapper(context, appTheme.alarmItem.timeTextStyle),
        null,
        R.attr.textAlarmTime,
    ).apply {
        setOnClickListener { delegate?.onTimeClick() }
    }

    private val day = MaterialTextView(context).apply {
        setOnClickListener { delegate?.onDayClick() }
    }

    private val switch = SwitchMaterial(context).apply {
        setOnCheckedChangeListener { _, isChecked ->
            delegate?.onSwitchChange(isChecked)
        }
    }

    private val melody = MaterialTextView(context, null, appTheme.alarmItem.melodyText.attr).apply {
        setOnClickListener { delegate?.onMelodyClick() }
        setOnLongClickListener {
            delegate?.onMelodyLongClick()
            true
        }
    }

    private val alarmIcon = ImageView(context, null, appTheme.alarmItem.alarmIcon.attr)

    private val btnDelete = ImageButton(
        ContextThemeWrapper(context, appTheme.alarmItem.actionButton),
        null,
        appTheme.alarmItem.actionButton,
    ).apply {
        setImageDrawable(ContextCompat.getDrawable(context, appTheme.alarmItem.iconDelete))
        setOnClickListener { delegate?.onDeleteClick() }
        setOnLongClickListener {
            delegate?.onDeleteLongClick()
            true
        }
    }

    private val monday = dayOfWeek(R.string.monday, 0)
    private val tuesday = dayOfWeek(R.string.tuesday, 1)
    private val wednesday = dayOfWeek(R.string.wednesday, 2)
    private val thursday = dayOfWeek(R.string.thursday, 3)
    private val friday = dayOfWeek(R.string.friday, 4)
    private val saturday = dayOfWeek(R.string.saturday, 5)
    private val sunday = dayOfWeek(R.string.sunday, 6)

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
        val bgColor = getResourceApi23(
            more = { resources.getColor(appTheme.alarmItem.bgColor, null) },
            less = { resources.getColor(appTheme.alarmItem.bgColor) },
        )
        background = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(bgColor, bgColor),
        )
        background.setStroke(2, Color.parseColor("#12000000"))
        background.cornerRadius = 16.0f
        clipChildren = false

        val x = 8
        val y = 2

        val xPadding = x.xdip
        val yPadding = y.ydip

        contourHeightOf { _ ->
            week.bottom() + yPadding
        }

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
        alarmIcon.layoutBy(
            leftTo { parent.left() + xPadding }.widthOf { 16.xdip },
            centerVerticallyTo { melody.centerY() }.heightOf { 16.ydip }
        )
        melody.layoutBy(
            leftTo { alarmIcon.right() }.rightTo { btnDelete.left() - xPadding },
            centerVerticallyTo { btnDelete.centerY() }.heightOf { btnDelete.height() }
        )
        week.layoutBy(
            matchParentX(x, y),
            topTo { btnDelete.bottom() + yPadding }
        )
    }

    private fun dayOfWeek(@StringRes strId: Int, id: Int) = MaterialTextView(
        ContextThemeWrapper(context, appTheme.alarmItem.dayOfWeekStyle),
        null,
        appTheme.alarmItem.dayOfWeekStyle,
    ).apply {
        layoutParams = LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT,
            1.0f
        )
        text = context.getString(strId)
        setOnClickListener { delegate?.onDayOfWeekClick(days[id]) }
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
        val bgDrawable = when {
            isActive && day == days.first() -> R.drawable.day_active_start
            isActive && day == days.last() -> R.drawable.day_active_end
            isActive -> R.drawable.day_active_middle
            else -> R.drawable.day_ripple_ltd
        }
        dayViews[day]?.setTextColor(getResourceApi23(
            more = { resources.getColor(textColor, null) },
            less = { resources.getColor(textColor) })
        )
        dayViews[day]?.background = ResourcesCompat.getDrawable(resources, bgDrawable, null)
    }

    override fun getBackground() = super.getBackground() as GradientDrawable
}
