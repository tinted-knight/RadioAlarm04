package com.noomit.radioalarm02.service.device_reboot

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.noomit.data.database.getAndroidSqlDriver
import com.noomit.data.database.getDatabase
import com.noomit.radioalarm02.util.ScheduleAlarmUtils

class ScheduleAlarmWorker(
    private val context: Context,
    workerParams: WorkerParameters,
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val sqlDriver = getAndroidSqlDriver(context)
        val database = getDatabase(sqlDriver)
        val scheduler = ScheduleAlarmUtils(context)
        database.alarmQueries.nextActive().executeAsOneOrNull()?.let {
            scheduler.schedule(
                alarmId = it.id,
                bellUrl = it.bell_url,
                bellName = it.bell_name,
                timeInMillis = it.time_in_millis
            )
        }

        return Result.success()
    }
}