package com.noomit.radioalarm02.ui.radio_browser.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noomit.domain.entities.CategoryModel
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.IStationItem
import com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter.StationItemView

abstract class CategoryListAdapter<Model : CategoryModel>(
    private val onClick: (Model) -> Unit,
    diffUtil: CategoryDiffUtil<Model>,
) :
    ListAdapter<Model, CategoryListViewHolder>(diffUtil) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        return CategoryListViewHolder(StationItemView(parent.context))
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
    fun bind(value: CategoryModel) = with(itemView as IStationItem) {
        setName(value.name)
        setCount(value.stationCount)
    }
}
