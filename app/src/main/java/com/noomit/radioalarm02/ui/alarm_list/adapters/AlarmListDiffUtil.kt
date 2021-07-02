package com.noomit.radioalarm02.ui.alarm_list.adapters

import androidx.recyclerview.widget.DiffUtil
import com.noomit.domain.entities.AlarmModel

enum class Payload { IsEnabled, DaysOfWeek, Time }

class AlarmListDiffUtil : DiffUtil.ItemCallback<AlarmModel>() {
    override fun areItemsTheSame(oldItem: AlarmModel, newItem: AlarmModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: AlarmModel, newItem: AlarmModel): Boolean {
        return (oldItem.timeInMillis == newItem.timeInMillis
                && oldItem.isEnabled == newItem.isEnabled
                && oldItem.daysOfWeek == newItem.daysOfWeek
                && oldItem.bellUrl == newItem.bellUrl)
    }

    override fun getChangePayload(oldItem: AlarmModel, newItem: AlarmModel): Any? {
        return when {
            oldItem.isEnabled != newItem.isEnabled -> Payload.IsEnabled
            oldItem.daysOfWeek != newItem.daysOfWeek -> Payload.DaysOfWeek
            oldItem.hour != newItem.hour || oldItem.minute != newItem.minute -> Payload.Time
            else -> null
        }
    }
}
