package com.noomit.radioalarm02.ui.alarm_list.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.model.hourString
import com.noomit.radioalarm02.model.isDayBitOn
import com.noomit.radioalarm02.model.minuteString
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

interface AlarmAdapterActions {
    fun onDeleteClick(alarm: Alarm)
    fun onDeleteLongClick(alarm: Alarm)
    fun onEnabledChecked(alarm: Alarm, isChecked: Boolean)
    fun onTimeClick(alarm: Alarm)
    fun onMelodyClick(alarm: Alarm)
    fun onMelodyLongClick(alarm: Alarm)
    fun onDayOfWeekClick(day: Int, alarm: Alarm)
}

class AlarmListAdapter(
    private val delegate: AlarmAdapterActions,
) : ListAdapter<Alarm, AlarmListViewHolder>(AlarmListDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AlarmListViewHolder(
        AlarmItemView(parent.context)
    )

    override fun onBindViewHolder(holder: AlarmListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewAttachedToWindow(holder: AlarmListViewHolder) {
        super.onViewAttachedToWindow(holder)
        val alarm = getItem(holder.adapterPosition)
        holder.contour.delegate = object : IAlarmItemActions {
            override fun onDeleteClick() = delegate.onDeleteClick(alarm)

            override fun onDeleteLongClick() = delegate.onDeleteLongClick(alarm)

            override fun onSwitchChange(isChecked: Boolean) =
                delegate.onEnabledChecked(alarm, isChecked)

            override fun onTimeClick() = delegate.onTimeClick(alarm)

            override fun onDayClick() = delegate.onTimeClick(alarm)

            override fun onMelodyClick() = delegate.onMelodyClick(alarm)

            override fun onMelodyLongClick() = delegate.onMelodyLongClick(alarm)

            override fun onDayOfWeekClick(day: Int) = delegate.onDayOfWeekClick(day, alarm)

        }
    }

    override fun onViewDetachedFromWindow(holder: AlarmListViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.contour.delegate = null
    }
}

class AlarmListDiffUtil : DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return (oldItem.time_in_millis == newItem.time_in_millis
                && oldItem.is_enabled == newItem.is_enabled
                && oldItem.days_of_week == newItem.days_of_week
                && oldItem.bell_url == newItem.bell_url)
    }

}

class AlarmListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val dateFormat = SimpleDateFormat("MMM, d", Locale.getDefault())
    private val timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)

    val contour: IAlarmItem
        get() = itemView as IAlarmItem

    fun bind(value: Alarm) {
        if (value.time_in_millis == 0L || !value.is_enabled) {
            contour time "${value.hourString}:${value.minuteString}"
            contour day ""
        } else {
            val date = Date(value.time_in_millis)
            contour time timeFormat.format(date)
            contour day dateFormat.format(date)
        }
        contour melody if (value.bell_url.isNotBlank()) value.bell_name else itemView.context.getString(R.string.melody_system)
        contour switch value.is_enabled
        processDaysOfWeek(value.days_of_week)
    }

    private fun processDaysOfWeek(daysOfWeek: Int) {
        IAlarmItem.days.forEach { day -> contour.checkDay(day, daysOfWeek.isDayBitOn(day)) }
    }
}
