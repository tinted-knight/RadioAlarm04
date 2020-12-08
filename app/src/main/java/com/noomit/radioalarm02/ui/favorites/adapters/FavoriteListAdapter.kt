package com.noomit.radioalarm02.ui.favorites.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noomit.radioalarm02.Favorite
import com.noomit.radioalarm02.R

typealias ItemClick = ((Favorite) -> Unit)
typealias ItemLongClick = ((Favorite) -> Unit)

class FavoriteListAdapter(
    private val onClick: ItemClick,
) : ListAdapter<Favorite, FavoritesListViewHolder>(FavoriteListDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = FavoritesListViewHolder(
        LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_category, parent, false)
    )

    override fun onBindViewHolder(holder: FavoritesListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewAttachedToWindow(holder: FavoritesListViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.itemView.setOnClickListener {
            onClick(getItem(holder.adapterPosition))
        }
    }

    override fun onViewDetachedFromWindow(holder: FavoritesListViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.setOnClickListener(null)
    }
}

private class FavoriteListDiffUtil : DiffUtil.ItemCallback<Favorite>() {
    override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
        return (oldItem.name == newItem.name)
    }

}

class FavoritesListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(value: Favorite) = with(itemView) {
        findViewById<TextView>(R.id.tv_station_name).text = value.name
    }
}
