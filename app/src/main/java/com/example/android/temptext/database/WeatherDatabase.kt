package com.example.android.temptext.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

abstract class WeatherDatabase: RoomDatabase() {
     abstract fun weatherDao(): WeatherDao


     companion object{
         @Volatile
         private var INSTANCE : WeatherDatabase? = null

         fun getInstance (context: Context):WeatherDatabase{

             var instance = INSTANCE

             if (instance == null){
                 instance = Room.databaseBuilder(
                     context,//.applicationContext,
                 WeatherDatabase::class.java,
                     "weather_database"
                 )
                     .fallbackToDestructiveMigration()
                     .build()

                 INSTANCE = instance
             }
                return INSTANCE as WeatherDatabase

         }
     }
}