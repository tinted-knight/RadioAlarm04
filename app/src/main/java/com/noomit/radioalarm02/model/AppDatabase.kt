package com.noomit.radioalarm02.model

import android.content.Context
import com.noomit.radioalarm02.Database
import com.squareup.sqldelight.android.AndroidSqliteDriver
import timber.log.Timber

private fun plog(message: String) =
    Timber.tag("tagg-database").i("$message [${Thread.currentThread().name}]")

class AppDatabase {
    companion object {
        @Volatile
        private var INSTANCE: Database? = null

        private const val DB_NAME = "favorites.db"

        fun getInstance(context: Context): Database {
            plog("getInstance, INSTANCE is ${if (INSTANCE == null) "null" else "not null"}")
            val tempInstance = INSTANCE
            if (tempInstance != null) return tempInstance

            synchronized(AppDatabase::class.java) {
                val instance = Database(
                    AndroidSqliteDriver(
                        schema = Database.Schema,
                        context = context,
                        name = DB_NAME,
                    ),
                )
                INSTANCE = instance
                return instance
            }
        }
    }
}