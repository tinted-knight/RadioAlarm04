package com.noomit.radioalarm02.ui.radio_browser.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.radiobrowser.ServerInfo
import com.noomit.radioalarm02.R

typealias ServerClick = (ServerInfo) -> Unit

class ServerListAdapter(
    private val onServerClick: ServerClick,
) : ListAdapter<ServerInfo, ServerListViewHolder>(ServerDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ServerListViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_server, parent, false)
    )

    override fun onBindViewHolder(holder: ServerListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onViewAttachedToWindow(holder: ServerListViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.apply {
            itemView.setOnClickListener {
                onServerClick(getItem(adapterPosition))
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: ServerListViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.setOnClickListener(null)
    }
}

private class ServerDiffUtil : DiffUtil.ItemCallback<ServerInfo>() {
    override fun areItemsTheSame(oldItem: ServerInfo, newItem: ServerInfo): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ServerInfo, newItem: ServerInfo): Boolean {
        return (oldItem.urlString == newItem.urlString
                && oldItem.isReachable == newItem.isReachable)
    }

}


class ServerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(value: ServerInfo) = with(itemView) {
        findViewById<TextView>(R.id.cb_title).text = value.urlString
    }
}
