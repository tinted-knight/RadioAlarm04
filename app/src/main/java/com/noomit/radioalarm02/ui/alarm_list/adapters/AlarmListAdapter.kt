package com.noomit.radioalarm02.ui.alarm_list.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noomit.domain.alarm_manager.isDayBitOn
import com.noomit.domain.entities.AlarmModel
import com.noomit.radioalarm02.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

interface AlarmAdapterActions {
    fun onDeleteClick(alarm: AlarmModel)
    fun onDeleteLongClick(alarm: AlarmModel)
    fun onEnabledChecked(alarm: AlarmModel, isChecked: Boolean)
    fun onTimeClick(alarm: AlarmModel)
    fun onMelodyClick(alarm: AlarmModel)
    fun onMelodyLongClick(alarm: AlarmModel)
    fun onDayOfWeekClick(day: Int, alarm: AlarmModel)
}

class AlarmListAdapter(
    private val delegate: AlarmAdapterActions,
) : ListAdapter<AlarmModel, AlarmListViewHolder>(AlarmListDiffUtil()) {
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

class AlarmListDiffUtil : DiffUtil.ItemCallback<AlarmModel>() {
    override fun areItemsTheSame(oldItem: AlarmModel, newItem: AlarmModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: AlarmModel, newItem: AlarmModel): Boolean {
        return (oldItem.timeInMillis == newItem.timeInMillis
                && oldItem.isEnabled == newItem.isEnabled
                && oldItem.daysOfWeek == newItem.daysOfWeek
                && oldItem.bellUrl == newItem.bellUrl)
    }

}

class AlarmListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val dateFormat = SimpleDateFormat("MMM, d", Locale.getDefault())
    private val timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)

    val contour: IAlarmItem
        get() = itemView as IAlarmItem

    fun bind(value: AlarmModel) {
        val date = Date(value.timeInMillis)
        // #todo instead of setDay(value!!) => showDay(value!!) and hideDay()
        when (value.timeInMillis == 0L || !value.isEnabled) {
            true -> contour.setDay(dateFormat.format(date))
            false -> contour.setDay("")
        }
        contour.setTime(timeFormat.format(date))
        contour.setMelody(if (value.bellUrl.isNotBlank()) value.bellName else itemView.context.getString(R.string.melody_system))
        contour.setSwitch(value.isEnabled)
        processDaysOfWeek(value.daysOfWeek)
    }

    private fun processDaysOfWeek(daysOfWeek: Int) {
        IAlarmItem.days.forEach { day -> contour.checkDay(day, daysOfWeek.isDayBitOn(day)) }
    }
}
