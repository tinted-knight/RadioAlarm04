package com.noomit.radioalarm02.ui.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.noomit.radioalarm02.R

class SimpleAlertDialog(
  @StringRes private val message: Int,
  private val onAccept: () -> Unit,
  private val onRefuse: () -> Unit,
) : DialogFragment() {
  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    return activity?.let {
      val builder = AlertDialog.Builder(it)
        .setMessage(message)
        .setPositiveButton(R.string.schedule_permission_accept) { _, _ -> onAccept() }
        .setNegativeButton(R.string.schedule_permission_refuse) { _, _ -> onRefuse() }
      builder.create()
    } ?: throw IllegalStateException("Activity is null while creating dialog")
  }
}
