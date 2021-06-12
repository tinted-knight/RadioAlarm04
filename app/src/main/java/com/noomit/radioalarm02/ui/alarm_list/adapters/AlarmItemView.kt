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
import com.noomit.radioalarm02.ui.alarm_list.adapters.IAlarmItem.Companion.FRI_ID
import com.noomit.radioalarm02.ui.alarm_list.adapters.IAlarmItem.Companion.MON_ID
import com.noomit.radioalarm02.ui.alarm_list.adapters.IAlarmItem.Companion.SAT_ID
import com.noomit.radioalarm02.ui.alarm_list.adapters.IAlarmItem.Companion.SUN_ID
import com.noomit.radioalarm02.ui.alarm_list.adapters.IAlarmItem.Companion.THU_ID
import com.noomit.radioalarm02.ui.alarm_list.adapters.IAlarmItem.Companion.TUE_ID
import com.noomit.radioalarm02.ui.alarm_list.adapters.IAlarmItem.Companion.WED_ID
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
    fun setTime(value: String)
    fun setDay(value: String)
    fun setSwitch(isChecked: Boolean)
    fun setMelody(value: String)
    fun checkDay(day: Int, isActive: Boolean)

    var delegate: IAlarmItemActions?

    companion object {
        val days = listOf(
            Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
            Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
        )
        const val MON_ID = 0
        const val TUE_ID = 1
        const val WED_ID = 2
        const val THU_ID = 3
        const val FRI_ID = 4
        const val SAT_ID = 5
        const val SUN_ID = 6
    }
}

// #achtung flashes when click on day
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

    private val monday = dayOfWeek(R.string.monday, MON_ID)
    private val tuesday = dayOfWeek(R.string.tuesday, TUE_ID)
    private val wednesday = dayOfWeek(R.string.wednesday, WED_ID)
    private val thursday = dayOfWeek(R.string.thursday, THU_ID)
    private val friday = dayOfWeek(R.string.friday, FRI_ID)
    private val saturday = dayOfWeek(R.string.saturday, SAT_ID)
    private val sunday = dayOfWeek(R.string.sunday, SUN_ID)

    private val dayViews: Map<Int, TextView> = mapOf(
        days[0] to monday,
        days[1] to tuesday,
        days[2] to wednesday,
        days[3] to thursday,
        days[4] to friday,
        days[5] to saturday,
        days[6] to sunday,
    )

    private val isWeekStartMonday = Calendar.getInstance().firstDayOfWeek == Calendar.MONDAY

    private val week = LinearLayout(context).apply {
        orientation = LinearLayout.HORIZONTAL
        if (isWeekStartMonday) {
            addView(monday)
            addView(tuesday)
            addView(wednesday)
            addView(thursday)
            addView(friday)
            addView(saturday)
            addView(sunday)
        } else {
            addView(sunday)
            addView(monday)
            addView(tuesday)
            addView(wednesday)
            addView(thursday)
            addView(friday)
            addView(saturday)
        }
    }

    init {
        val bgColor = ResourcesCompat.getColor(resources, appTheme.alarmItem.bgColor, null)
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

        contourHeightOf {
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
            matchParentX(x, x),
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

    override fun setTime(value: String) {
        time.text = value
    }

    override fun setDay(value: String) {
        day.text = value
    }

    override fun setSwitch(isChecked: Boolean) {
        switch.isChecked = isChecked
    }

    override fun setMelody(value: String) {
        melody.text = value
    }

    override fun checkDay(day: Int, isActive: Boolean) {
        val textColor = when {
            isActive -> R.color.colorDayTextActive
            else -> R.color.colorDayTextInactive
        }

        val bgDrawable = getDayBackground(isActive, day)

        dayViews[day]?.setTextColor(ResourcesCompat.getColor(resources, textColor, null))
        dayViews[day]?.background = ResourcesCompat.getDrawable(resources, bgDrawable, null)
    }

    override fun getBackground() = super.getBackground() as GradientDrawable

    private fun getDayBackground(isActive: Boolean, day: Int): Int {
        return if (isWeekStartMonday) {
            dayBackgroundFor(day, isActive, weekStart = days.first(), weekEnd = days.last())
        } else {
            dayBackgroundFor(day, isActive, weekStart = days.last(), weekEnd = days[5])
        }
    }

    private fun dayBackgroundFor(day: Int, isActive: Boolean, weekStart: Int, weekEnd: Int): Int {
        return when {
            isActive && day == weekStart -> R.drawable.day_active_start
            isActive && day == weekEnd -> R.drawable.day_active_end
            isActive -> R.drawable.day_active_middle
            else -> R.drawable.day_ripple_ltd
        }
    }
}
