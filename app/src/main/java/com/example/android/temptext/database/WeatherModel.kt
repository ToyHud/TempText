package com.example.android.temptext.database

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//represent tables in app's database

@Entity(tableName = "userInfo")
data class WeatherModel(
    @PrimaryKey @NonNull @ColumnInfo(name = "id") val userId: String,
    @ColumnInfo(name = "location") val currentLocation: String,
    @ColumnInfo(name = "celsius") val celsius: Float,
    @ColumnInfo(name = "fahrenheit") val fahrenheit: Float,
    @ColumnInfo(name = "city") val cityName: String,
    @ColumnInfo(name = "condition") val currentWeatherCondition: String,
    @ColumnInfo(name = "precipitation") val precipitation: Float,
    @ColumnInfo(name = "humidity") val humidity: Double,
    @ColumnInfo(name = "air_quality") val aqi: String,
    @ColumnInfo(name = "wind_mph") val wind: Float,
)

data class TempTuple(
    @ColumnInfo(name = "celsius") val celsius: Float,
    @ColumnInfo(name = "fahrenheit") val fahrenheit: Float,
)
/*class UniqueIdTypeConverter(){
    @TypeConverter
    fun toWeatherEntity(entityType: String?): WeatherModel{
        return
    }
}*/








