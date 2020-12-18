package com.noomit.radioalarm02.data

import android.content.Context
import com.noomit.radioalarm02.Database
import com.noomit.radioalarm02.tplog
import com.squareup.sqldelight.android.AndroidSqliteDriver

class AppDatabase {
    companion object {
        @Volatile
        private var INSTANCE: Database? = null

        private const val DB_NAME = "favorites.db"

        fun getInstance(context: Context): Database {
            tplog("getInstance, INSTANCE is ${if (INSTANCE == null) "null" else "not null"}")
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
