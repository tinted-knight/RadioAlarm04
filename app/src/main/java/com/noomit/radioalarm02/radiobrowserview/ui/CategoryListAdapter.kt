package com.noomit.radioalarm02.radiobrowserview.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.radiobrowserview.model.LanguageModel

typealias ItemClickListener = ((LanguageModel) -> Unit)

class CategoryListAdapter(private val onClick: ItemClickListener) :
    ListAdapter<LanguageModel, CategoryListViewHolder>(MyDiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        return CategoryListViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.item_category, parent, false)
        )
    }

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewAttachedToWindow(holder: CategoryListViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.setOnClickListener {
            onClick(getItem(holder.adapterPosition))
        }
    }

    override fun onViewDetachedFromWindow(holder: CategoryListViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.setOnClickListener(null)
    }
}

private class MyDiffUtilCallback : DiffUtil.ItemCallback<LanguageModel>() {
    override fun areItemsTheSame(oldItem: LanguageModel, newItem: LanguageModel): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: LanguageModel, newItem: LanguageModel): Boolean {
        return (oldItem.name == newItem.name && oldItem.stationCount == newItem.stationCount)
    }

}

class CategoryListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(value: LanguageModel) = with(itemView) {
        findViewById<TextView>(R.id.tv_station_name).text = value.name
        findViewById<TextView>(R.id.tv_station_count).text = value.stationCount
    }
}