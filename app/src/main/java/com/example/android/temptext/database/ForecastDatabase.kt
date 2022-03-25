package com.example.android.temptext.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WeatherModel::class ,FutureWeatherModel::class], version =1)


     abstract class ForecastDatabase:RoomDatabase() {

         public abstract fun weatherDao(): WeatherDao?
         public abstract fun futureweatherDao(): FutureWeatherDao?


         companion object {
             @Volatile
             private var INSTANCE: ForecastDatabase? = null


             fun getForecastDatabase(context: Context): ForecastDatabase? {


                 if (INSTANCE == null) {
                     INSTANCE = Room.databaseBuilder(
                         context.applicationContext, ForecastDatabase::class.java,
                         "ForecastDatabase"
                     )
                         .allowMainThreadQueries()
                         .build()


                 }
                 return INSTANCE

             }
         }

     }