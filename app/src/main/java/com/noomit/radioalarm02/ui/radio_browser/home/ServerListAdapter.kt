package com.noomit.radioalarm02.ui.radio_browser.home

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noomit.domain.radio_browser.ServerInfo

typealias ServerClick = (ServerInfo) -> Unit

class ServerListAdapter(
  private val onServerClick: ServerClick,
) : ListAdapter<ServerInfo, ServerListViewHolder>(ServerDiffUtil()) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerListViewHolder {
    return ServerListViewHolder(ServerItemView(parent.context))
  }

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
  fun bind(value: ServerInfo) = with(itemView as IServerItem) {
    setName(value.urlString)
  }
}
