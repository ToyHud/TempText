package com.example.android.temptext.database

import androidx.room.*

@Dao
interface FutureWeatherDao

    {@Query("SELECT * FROM userinfo ORDER BY id DESC")
     fun getAllUserInfo():List<FutureWeatherModel>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(futureWeatherModel: List <FutureWeatherModel>)

    @Delete
    fun deleteUser(futureWeatherModel: List<FutureWeatherModel>)


    @Update
    fun updateUser (futureWeatherModel: List<FutureWeatherModel>)

}