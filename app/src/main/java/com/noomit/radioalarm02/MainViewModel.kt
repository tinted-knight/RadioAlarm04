package com.noomit.radioalarm02

import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.noomit.radioalarm02.ui.dialog.SimpleAlertDialog

class MainViewModel : ViewModel() {
  /** Android 12+ requires special permission to schedule exact alarms */
  fun requestSchedulePermission(context: Activity, fm: FragmentManager) {
    val systemAlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !systemAlarmManager.canScheduleExactAlarms()) {
      SimpleAlertDialog(
        message = R.string.schedule_permission_explanation,
        onAccept = {
          context.startActivity(
            Intent().apply { action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM },
          )
        },
        onRefuse = { context.finish() },
      ).show(fm, "dialog-tag")
    }
  }
}
