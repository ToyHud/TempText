package com.example.android.temptext.database

import androidx.lifecycle.LiveData
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query


interface WeatherDao {
    @Query("SELECT * FROM WeatherImages")
    fun getAll():LiveData<List<WeatherImageEntity>>

    @Query("SELECT * FROM WeatherImages ORDER BY id DESC LIMIT 1")
    fun getMostRecentlyAddWeather (): WeatherImageEntity

    @Query ( "DELETE from WeatherImages where id = (select max (id)-1 from WeatherImages)")
    suspend fun deleteWeather()

    @Insert
    suspend fun addWeatherImage (weatherImageEntity: WeatherImageEntity)

    @Delete
    suspend fun deleteUser (user: String)
}