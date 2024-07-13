package com.t27.continental.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.t27.continental.data.daos.LocationDao
import com.t27.continental.data.daos.ShoppingListDao
import com.t27.continental.data.models.Location
import com.t27.continental.data.models.Product

@Database(entities = [Location::class, Product::class], version = 1, exportSchema = false)
abstract class database : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        @Volatile
        private var Instance: database? = null

        fun getDatabase(context: Context): database {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    database::class.java,
                    "continental.db"
                ).build().also { Instance = it }
            }
        }
    }
}