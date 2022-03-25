package com.example.android.temptext.database


import androidx.room.*

@Dao
interface WeatherDao {

    @Query("SELECT * FROM userinfo ORDER BY id DESC")
     fun getAllUserInfo():List<WeatherModel>


     @Insert
     fun insertUser(user: WeatherModel?)

     @Delete
     fun deleteUser(user: WeatherModel?)


     @Update
     fun updateUser (user: WeatherModel?)


}