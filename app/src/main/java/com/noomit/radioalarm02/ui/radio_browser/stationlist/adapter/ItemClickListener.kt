package com.noomit.radioalarm02.ui.radio_browser.stationlist.adapter

interface ItemClickListener<T> {
  fun onClick(item: T)
  fun onLongClick(item: T)
}
