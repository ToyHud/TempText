package com.example.android.temptext.database;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "userinfo")
public class FutureWeatherModel (
    @PrimaryKey(autoGenerate = true)@ColumnInfo(name= "id") val id: Int = 0,
    @ColumnInfo(name = "location") val currentLocation :String,
    @ColumnInfo(name = "temp_c") val celsius : Float?,

    )



