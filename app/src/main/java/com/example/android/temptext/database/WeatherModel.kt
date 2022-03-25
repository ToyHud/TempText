package com.example.android.temptext.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.concurrent.locks.Condition


@Entity(tableName = "userinfo")
data class WeatherModel(
    @PrimaryKey(autoGenerate = true)@ColumnInfo (name= "id") val id: Int = 0,
    @ColumnInfo(name = "location") val currentLocation :String,
    @ColumnInfo(name = "current") val currentWeather: String,
    @ColumnInfo(name = "temp_c") val celsius : Float?,
    @ColumnInfo(name =  "name") val city: String?,
    //@ColumnInfo(name = "condition") val currentWeatherCondition: WeatherConditions?,
    @ColumnInfo(name = "precip_in")val precipitation: Float?,
    @ColumnInfo(name = "humidity") val humidity:Double?,
    //@ColumnInfo(name = "air_quality")val aqi: WeatherConditions?,
    @ColumnInfo(name = "wind-mph") val wind: Float?,
    @ColumnInfo(name = "text") val currentCondition:String?,
    @ColumnInfo(name = "co")val ozone : Float?
)










