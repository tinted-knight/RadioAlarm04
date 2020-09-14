package com.noomit.radioalarm02.ui

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

typealias TimeClickListener = ((Alarm) -> Unit)
typealias DeleteClickListener = ((Alarm) -> Unit)
typealias DeleteLongClickListener = ((Alarm) -> Unit)
typealias MelodyClickListener = ((Alarm) -> Unit)
typealias MelodyLongClickListener = ((Alarm) -> Unit)

class AlarmListAdapter(
    private val deleteClickListener: DeleteClickListener,
    private val deleteLonglickListener: DeleteLongClickListener,
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
            btnDelete.setOnClickListener { deleteClickListener(getItem(adapterPosition)) }
            btnDelete.setOnLongClickListener {
                deleteLonglickListener(getItem(adapterPosition))
                return@setOnLongClickListener true
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: AlarmListViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.apply {
            btnDelete.setOnClickListener(null)
            btnDelete.setOnLongClickListener(null)
        }
    }
}

class AlarmListDiffUtil : DiffUtil.ItemCallback<Alarm>() {
    override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean {
        return (oldItem.time_in_millis == newItem.time_in_millis
                && oldItem.is_enabled == newItem.is_enabled)
    }

}

class AlarmListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val btnDelete: ImageButton = itemView.findViewById(R.id.imbtn_delete)

    fun bind(value: Alarm) = with(itemView) {
        findViewById<TextView>(R.id.tv_time).text = "${value.hour}:${value.minute}"
        findViewById<SwitchCompat>(R.id.sw_enabled).isChecked = value.is_enabled ?: false
    }
}