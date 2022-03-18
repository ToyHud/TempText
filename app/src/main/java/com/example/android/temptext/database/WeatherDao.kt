package com.example.android.temptext.database


import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.temptext.database.WeatherModel

interface WeatherDao {

    @Query("SELECT * FROM userinfo ORDER BY id DESC")
     fun getAllUserInfo():List<WeatherModel>


     @Insert
     fun insertUser(user: WeatherModel?)

     @Delete
     fun deleteUser(user:WeatherModel?)


     @Update
     fun updateUser (user: WeatherModel?)


}