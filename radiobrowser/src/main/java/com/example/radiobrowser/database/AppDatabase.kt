package com.example.radiobrowser.database

import android.content.Context
import com.noomit.domain.FavoriteQueries
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import fav_new.db.App2Database

//class AppDatabase {
//    companion object {
//        @Volatile
//        private var INSTANCE: Database? = null
//
//        private const val DB_NAME = "favorites.db"
//
//        fun getInstance(context: Context): Database {
//            val tempInstance = INSTANCE
//            if (tempInstance != null) return tempInstance
//
//            synchronized(AppDatabase::class.java) {
//                val instance = Database(
//                    AndroidSqliteDriver(
//                        schema = Database.Schema,
//                        context = context,
//                        name = DB_NAME,
//                    ),
//                )
//                INSTANCE = instance
//                return instance
//            }
//        }
//    }
//}

fun getAndroidSqlDriver(context: Context) = AndroidSqliteDriver(
    schema = App2Database.Schema,
    context = context,
    name = "fav_new.db"
)

fun getDatabase(driver: SqlDriver): App2Database = App2Database(driver)

fun getFavoritesQueries(driver: SqlDriver): FavoriteQueries {
    return App2Database(driver).favoriteQueries
}
