package com.noomit.radioalarm02.home.adapters

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
import com.noomit.radioalarm02.model.hourString
import com.noomit.radioalarm02.model.isDayBitOn
import com.noomit.radioalarm02.model.minuteString
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

typealias TimeClick = ((Alarm) -> Unit)
typealias DeleteClick = ((Alarm) -> Unit)
typealias DeleteLongClick = ((Alarm) -> Unit)
typealias MelodyClick = ((Alarm) -> Unit)
typealias MelodyLongClick = ((Alarm) -> Unit)
typealias DayOfWeekClick = ((Int, Alarm) -> Unit)
typealias EnabledSwitch = ((Alarm, Boolean) -> Unit)

class AlarmListAdapter(
    private val onDeleteClick: DeleteClick,
    private val onDeleteLongClick: DeleteLongClick,
    private val onDayClick: DayOfWeekClick,
    private val onTimeClick: TimeClick,
    private val onEnabledChecked: EnabledSwitch,
    private val onMelodyClick: MelodyClick,
    private val onMelodyLongClick: MelodyLongClick,
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
            btnDelete.setOnClickListener { onDeleteClick(alarm) }
            btnDelete.setOnLongClickListener {
                onDeleteLongClick(alarm)
                return@setOnLongClickListener true
            }
            tvSun.setOnClickListener { onDayClick(1, alarm) }
            tvMon.setOnClickListener { onDayClick(2, alarm) }
            tvTue.setOnClickListener { onDayClick(3, alarm) }
            tvWed.setOnClickListener { onDayClick(4, alarm) }
            tvThu.setOnClickListener { onDayClick(5, alarm) }
            tvFri.setOnClickListener { onDayClick(6, alarm) }
            tvSat.setOnClickListener { onDayClick(7, alarm) }

            swEnabled.setOnCheckedChangeListener { _, isChecked ->
                onEnabledChecked(alarm, isChecked)
            }

            tvTime.setOnClickListener { onTimeClick(alarm) }
            tvDay.setOnClickListener { onTimeClick(alarm) }

            tvMelody.setOnClickListener { onMelodyClick(alarm) }
            tvMelody.setOnLongClickListener {
                onMelodyLongClick(alarm)
                return@setOnLongClickListener true
            }
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

            swEnabled.setOnCheckedChangeListener(null)

            tvTime.setOnClickListener(null)
            tvDay.setOnClickListener(null)

            tvMelody.setOnClickListener(null)
            tvMelody.setOnLongClickListener(null)
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
                && oldItem.bell_url == newItem.bell_url)
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

    // #think should not be here
    private val dateFormat = SimpleDateFormat("MMM, d", Locale.getDefault())
    private val timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)

    fun bind(value: Alarm) {
        if (value.time_in_millis == 0L || !value.is_enabled) {
            tvDay.text = ""
            tvTime.text = "${value.hourString}:${value.minuteString}"
        } else {
            val date = Date(value.time_in_millis)
            tvTime.text = timeFormat.format(date)
            tvDay.text = dateFormat.format(date)
        }
        tvMelody.text = value.bell_name
        swEnabled.isChecked = value.is_enabled ?: false
        processDaysOfWeek(value.days_of_week)
    }

    private fun processDaysOfWeek(daysOfWeek: Int) {
        days.forEachIndexed { index, day ->
            val isBitOn = daysOfWeek.isDayBitOn(day)
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