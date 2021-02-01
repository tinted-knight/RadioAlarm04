package com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noomit.domain.StationModel

class StationListAdapter(
    private val delegate: ItemClickListener<StationModel>,
) :
    ListAdapter<StationModel, StationListViewHolder>(StationListDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StationListViewHolder(
        StationItemView(parent.context)
    )

    override fun onBindViewHolder(holder: StationListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewAttachedToWindow(holder: StationListViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.setOnClickListener {
            delegate.onClick(getItem(holder.adapterPosition))
        }
        holder.itemView.setOnLongClickListener {
            delegate.onLongClick(getItem(holder.adapterPosition))
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
    fun bind(value: StationModel) = (itemView as IStationItem).apply {
        setName(value.name)
        // #todo count not needed for favorites screen
        setCount(value.upvotes)
    }
}
