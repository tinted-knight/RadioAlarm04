package com.noomit.radioalarm02.ui.alarm_list.adapters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.noomit.domain.entities.AlarmModel
import com.noomit.radioalarm02.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

interface AlarmAdapterActions {
  fun onDeleteClick(alarm: AlarmModel)
  fun onDeleteLongClick(alarm: AlarmModel)
  fun onEnabledChecked(alarm: AlarmModel, isChecked: Boolean)
  fun onTimeClick(alarm: AlarmModel)
  fun onMelodyClick(alarm: AlarmModel)
  fun onMelodyLongClick(alarm: AlarmModel)
  fun onDayOfWeekClick(day: Int, alarm: AlarmModel)
}

class AlarmListAdapter(
  private val delegate: AlarmAdapterActions,
) : ListAdapter<AlarmModel, AlarmListViewHolder>(AlarmListDiffUtil()) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AlarmListViewHolder(
    AlarmItemView(parent.context),
  )

  override fun onBindViewHolder(
    holder: AlarmListViewHolder,
    position: Int,
    payloads: MutableList<Any>,
  ) {
    if (payloads.isNullOrEmpty()) {
      holder.bind(getItem(position))
      return
    }
    holder.bind(getItem(position), payloads)
  }

  override fun onBindViewHolder(holder: AlarmListViewHolder, position: Int) {
    holder.bind(getItem(position))
  }

  override fun onViewAttachedToWindow(holder: AlarmListViewHolder) {
    super.onViewAttachedToWindow(holder)
    // `getItem` _must_ be function
    // if not delegate captures initially loaded item, what causes bugs
    val getItem = { getItem(holder.adapterPosition) }
    holder.contour.delegate = object : IAlarmItemActions {
      override fun onDeleteClick() = delegate.onDeleteClick(getItem())

      override fun onDeleteLongClick() = delegate.onDeleteLongClick(getItem())

      override fun onSwitchChange(isChecked: Boolean) =
        delegate.onEnabledChecked(getItem(), isChecked)

      override fun onTimeClick() = delegate.onTimeClick(getItem())

      override fun onDayClick() = delegate.onTimeClick(getItem())

      override fun onMelodyClick() = delegate.onMelodyClick(getItem())

      override fun onMelodyLongClick() = delegate.onMelodyLongClick(getItem())

      override fun onDayOfWeekClick(day: Int) = delegate.onDayOfWeekClick(day, getItem())

    }
  }

  override fun onViewDetachedFromWindow(holder: AlarmListViewHolder) {
    super.onViewDetachedFromWindow(holder)
    holder.contour.delegate = null
  }
}

class AlarmListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  private val dateFormat = SimpleDateFormat("MMM, d", Locale.getDefault())
  private val timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)

  val contour: IAlarmItem
    get() = itemView as IAlarmItem

  fun bind(alarm: AlarmModel) {
    processDateTime(alarm)
    contour.setMelody(
      if (alarm.bellUrl.isNotBlank()) alarm.bellName else itemView.context.getString(
        R.string.melody_system,
      ),
    )
    contour.setSwitch(alarm.isEnabled)
    processDaysOfWeek(alarm)
  }

  fun bind(alarm: AlarmModel, payloads: MutableList<Any>) {
    when (payloads[0]) {
      Payload.IsEnabled  -> {
        contour.setSwitch(alarm.isEnabled)
        processDateTime(alarm)
        processDaysOfWeek(alarm)
      }

      Payload.DaysOfWeek -> bind(alarm)
      Payload.Time       -> processDateTime(alarm)
    }
  }

  private fun processDateTime(alarm: AlarmModel) {
    val date = Date(alarm.timeInMillis)
    // #todo instead of setDay(value!!) => showDay(value!!) and hideDay()
    when (alarm.isEnabled) {
      true  -> contour.setDay(dateFormat.format(date))
      false -> contour.setDay("")
    }
    contour.setTime(timeFormat.format(date))
  }

  private fun processDaysOfWeek(alarm: AlarmModel) {
    alarm.newDaysOfWeek.forEach { contour.checkDay(it.key, it.value) }
  }
}
