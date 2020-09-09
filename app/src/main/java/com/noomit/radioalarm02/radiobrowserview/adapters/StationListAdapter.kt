package com.noomit.radioalarm02.radiobrowserview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.model.StationModel

typealias StationClickListener = ((StationModel) -> Unit)
typealias StationLongClickListener = ((StationModel) -> Unit)

class StationListAdapter(
    private val onClick: StationClickListener,
    private val onLongClick: StationLongClickListener,
) :
    ListAdapter<StationModel, StationListViewHolder>(StationListDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationListViewHolder {
        return StationListViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_category, parent, false)
        )
    }

    override fun onBindViewHolder(holder: StationListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewAttachedToWindow(holder: StationListViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.setOnClickListener {
            onClick(getItem(holder.adapterPosition))
        }
        holder.itemView.setOnLongClickListener {
            onLongClick(getItem(holder.adapterPosition))
            true
        }
    }

    override fun onViewDetachedFromWindow(holder: StationListViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.setOnClickListener(null)
        holder.itemView.setOnLongClickListener(null)
    }
}

private class StationListDiffUtil : DiffUtil.ItemCallback<StationModel>() {
    override fun areItemsTheSame(oldItem: StationModel, newItem: StationModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: StationModel, newItem: StationModel): Boolean {
        return (oldItem.name == newItem.name && oldItem.upvotes == newItem.upvotes)
    }

}

class StationListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(value: StationModel) = with(itemView) {
        findViewById<TextView>(R.id.tv_station_name).text = value.name
        findViewById<TextView>(R.id.tv_station_count).text = value.upvotes
    }
}