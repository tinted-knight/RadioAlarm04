package com.noomit.radioalarm02.ui

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noomit.radioalarm02.Alarm
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.model.days
import java.util.*

typealias TimeClickListener = ((Alarm) -> Unit)
typealias DeleteClickListener = ((Alarm) -> Unit)
typealias DeleteLongClickListener = ((Alarm) -> Unit)
typealias MelodyClickListener = ((Alarm) -> Unit)
typealias MelodyLongClickListener = ((Alarm) -> Unit)
typealias DayOfWeekClickListener = ((Int, Alarm) -> Unit)

class AlarmListAdapter(
    private val deleteClickListener: DeleteClickListener,
    private val deleteLonglickListener: DeleteLongClickListener,
    private val dayOfWeekClickListener: DayOfWeekClickListener,
) : ListAdapter<Alarm, AlarmListViewHolder>(AlarmListDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AlarmListViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alarm, parent, false)
    )

    override fun onBindViewHolder(holder: AlarmListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewAttachedToWindow(holder: AlarmListViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.apply {
            val alarm = getItem(adapterPosition)
            btnDelete.setOnClickListener { deleteClickListener(alarm) }
            btnDelete.setOnLongClickListener {
                deleteLonglickListener(alarm)
                return@setOnLongClickListener true
            }
            tvSun.setOnClickListener { dayOfWeekClickListener(1, alarm) }
            tvMon.setOnClickListener { dayOfWeekClickListener(2, alarm) }
            tvTue.setOnClickListener { dayOfWeekClickListener(3, alarm) }
            tvWed.setOnClickListener { dayOfWeekClickListener(4, alarm) }
            tvThu.setOnClickListener { dayOfWeekClickListener(5, alarm) }
            tvFri.setOnClickListener { dayOfWeekClickListener(6, alarm) }
            tvSat.setOnClickListener { dayOfWeekClickListener(7, alarm) }
        }
    }

    override fun onViewDetachedFromWindow(holder: AlarmListViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.apply {
            btnDelete.setOnClickListener(null)
            btnDelete.setOnLongClickListener(null)
            tvSun.setOnClickListener(null)
            tvMon.setOnClickListener(null)
            tvTue.setOnClickListener(null)
            tvWed.setOnClickListener(null)
            tvThu.setOnClickListener(null)
            tvFri.setOnClickListener(null)
            tvSat.setOnClickListener(null)
        }
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
                && oldItem.bell_id == newItem.bell_id)
    }

}

class AlarmListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val tvTime: TextView = itemView.findViewById(R.id.tv_time)
    val tvDay: TextView = itemView.findViewById(R.id.tv_day)
    val tvMelody: TextView = itemView.findViewById(R.id.tv_melody)
    val swEnabled: SwitchCompat = itemView.findViewById(R.id.sw_enabled)
    val btnDelete: ImageButton = itemView.findViewById(R.id.imbtn_delete)

    // days of week
    val tvMon: TextView = itemView.findViewById(R.id.tv_mon)
    val tvTue: TextView = itemView.findViewById(R.id.tv_tue)
    val tvWed: TextView = itemView.findViewById(R.id.tv_wed)
    val tvThu: TextView = itemView.findViewById(R.id.tv_thu)
    val tvFri: TextView = itemView.findViewById(R.id.tv_fri)
    val tvSat: TextView = itemView.findViewById(R.id.tv_sat)
    val tvSun: TextView = itemView.findViewById(R.id.tv_sun)

    private val dayViews = listOf(tvMon, tvTue, tvWed, tvThu, tvFri, tvSat, tvSun)

    fun bind(value: Alarm) {
        tvTime.text = "${value.hour}:${value.minute}"
        tvDay.text = "" // #todo dateString
        tvMelody.text = value.bell_name
        swEnabled.isChecked = value.is_enabled ?: false
        processDaysOfWeek(value.days_of_week)
    }

    private fun processDaysOfWeek(daysOfWeek: Int) {
        days.forEachIndexed { index, day ->
            val isBitOn = isDayBitOn(day, daysOfWeek)
            val textColor = when {
                isBitOn -> R.color.colorDayTextActive
                else -> R.color.colorDayTextInactive
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dayViews[index].setTextColor(itemView.resources.getColor(textColor, null))
            } else {
                dayViews[index].setTextColor(itemView.resources.getColor(textColor))
            }
        }
    }

    companion object {
        val days = listOf(
            Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
            Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
        )
    }
}

private fun isDayBitOn(calendarDay: Int, daysOfWeek: Int): Boolean {
    if (calendarDay == 1) return days[6] and daysOfWeek == days[6]

    return (days[calendarDay - 2] and daysOfWeek) == days[calendarDay - 2]
}