package com.example.radiobrowser.database

import android.content.Context
import com.example.radiobrowser.Database
import com.squareup.sqldelight.android.AndroidSqliteDriver

class AppDatabase {
    companion object {
        @Volatile
        private var INSTANCE: Database? = null

        private const val DB_NAME = "favorites.db"

        fun getInstance(context: Context): Database {
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
