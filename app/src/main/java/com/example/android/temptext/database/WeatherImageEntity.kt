package com.example.android.temptext.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json


@Entity(tableName = "WeatherImages")
data class WeatherImageEntity(
    @PrimaryKey(autoGenerate = true)
    @Transient
    @ColumnInfo(name = "id")
    val id: Int? = null,

    @Json(name = "message")
    @ColumnInfo(name = "image_url")
    val message: String,

    @Json(name = "status")
    @ColumnInfo(name = "status")
    val statusResponse: String

)
