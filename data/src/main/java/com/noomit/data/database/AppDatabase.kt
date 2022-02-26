package com.noomit.data.database

import android.content.Context
import com.noomit.db.AppDatabase
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver

// #todo hardcoded DB name
fun getAndroidSqlDriver(context: Context) = AndroidSqliteDriver(
  schema = AppDatabase.Schema,
  context = context,
  name = "fav_new.db"
)

fun getDatabase(driver: SqlDriver): AppDatabase = AppDatabase(driver)
