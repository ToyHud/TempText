package com.example.android.temptext.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room

@Database(
    entities = [WeatherModel::class ,FutureWeatherModel::class], version =1),




    public abstract fun weatherDao():WeatherDao?


companion object{
         @Volatile
         private var INSTANCE : WeatherDatabase? = null


         fun getWeatherDatabase(context: Context):WeatherDatabase?{



             if (INSTANCE == null){
                 INSTANCE = Room.databaseBuilder<WeatherDatabase>(
                     context.applicationContext, WeatherDatabase::class.java,
                     "WeatherDatabase"
                 )
                     .allowMainThreadQueries()
                     .build()


             }
                return INSTANCE

         }
     }

