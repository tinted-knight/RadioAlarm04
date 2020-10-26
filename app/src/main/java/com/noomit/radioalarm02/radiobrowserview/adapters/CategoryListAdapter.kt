package com.noomit.radioalarm02.radiobrowserview.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noomit.radioalarm02.R
import com.noomit.radioalarm02.model.CategoryModel

abstract class CategoryListAdapter<Model : CategoryModel>(
    private val onClick: (Model) -> Unit,
    diffUtil: CategoryDiffUtil<Model>,
) :
    ListAdapter<Model, CategoryListViewHolder>(diffUtil) {
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

abstract class CategoryDiffUtil<Model : CategoryModel> : DiffUtil.ItemCallback<Model>() {
    override fun areItemsTheSame(oldItem: Model, newItem: Model): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Model, newItem: Model): Boolean {
        return (oldItem.name == newItem.name && oldItem.stationCount == newItem.stationCount)
    }

}

class CategoryListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(value: CategoryModel) = with(itemView) {
        findViewById<TextView>(R.id.tv_station_name).text = value.name
        findViewById<TextView>(R.id.tv_station_count).text = value.stationCount
    }
}
