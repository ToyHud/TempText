package com.example.android.temptext.database


import androidx.room.*
import kotlinx.coroutines.flow.Flow

//provide methods that app uses to query, update, insert, and delete data in the database

@Dao
interface WeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: WeatherModel)

    @Update
    fun updateUserInfo(vararg user: WeatherModel)

    @Delete
    fun deleteUser(user: WeatherModel)

    @Query("SELECT * FROM userInfo ORDER BY uid DESC")
    fun getAllUserInfo(): Flow<List<WeatherModel>>

    @Query("SELECT fahrenheit, celsius FROM userInfo")
    fun loadTemperature(): Flow<TempTuple>

}