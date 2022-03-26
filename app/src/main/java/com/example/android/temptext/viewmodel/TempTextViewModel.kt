package com.example.android.temptext.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.temptext.network.WeatherAlertApi
import com.example.android.temptext.network.WeatherResponse
import kotlinx.coroutines.launch

class TempTextViewModel : ViewModel() {
    private var _apiResponse = MutableLiveData<WeatherResponse>()
    val apiResponse = _apiResponse


    private fun getCurrentWeatherResponse(apiKey: String, area: String, aqi: String) {
        try {
            viewModelScope.launch {
                apiResponse.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey,area,aqi)
            }
        } catch (e: Exception) {
            "Failure: ${e.message}"
        }
    }
    fun showCurrentWeather(apiKey: String, appArea: String, aqi: String) {
        getCurrentWeatherResponse(apiKey, appArea, aqi)
    }
   /* fun insertWeatherData(user: WeatherModel){
        viewModelScope.launch { weatherDao.insertUser(user) }
    }
    fun deleteUserData(user: WeatherModel){
        viewModelScope.launch { weatherDao.deleteUser(user) }
    }
    fun getAllUsers(user: WeatherModel): LiveData<List<WeatherModel>>{
        return weatherDao.getAllUserInfo().asLiveData()
    }*/
}

/*
class WeatherViewModelFactory(private val weatherDao: WeatherDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TempTextViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TempTextViewModel(weatherDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}*/