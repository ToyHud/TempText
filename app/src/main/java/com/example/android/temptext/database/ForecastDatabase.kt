package com.example.android.temptext.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// holds the database and serves as the main access point for the underlying connection to app's persisted data.
@Database(entities = [WeatherModel::class], version = 2)
abstract class ForecastDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao?

    companion object {
        @Volatile
        private var INSTANCE: ForecastDatabase? = null

        fun getForecastDatabase(context: Context): ForecastDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ForecastDatabase::class.java,
                    "ForecastDatabase"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}

