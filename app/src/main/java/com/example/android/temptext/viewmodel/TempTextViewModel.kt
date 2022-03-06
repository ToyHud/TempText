package com.example.android.temptext.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch

class TempTextViewModel : ViewModel(){
    private val apiKey: String= ""
    private val area: String= ""
    private val _city = MutableLiveData<String>()
    val city: LiveData<String> = _city
    private val _state = MutableLiveData<String>()
    val state: LiveData<String> = _state
    private val _currentWeather= MutableLiveData<String>()
    val currentWeather: LiveData<String> = _currentWeather
    private val _weatherIcon = MutableLiveData<String>()
    val weatherIcon: LiveData<String> = _weatherIcon
    private val _celsius = MutableLiveData<Float>()
    val celsius: LiveData<Float> = _celsius
    private val _fahrenheit = MutableLiveData<Float>()
    val fahrenheit: LiveData<Float> = _fahrenheit

    init {

        getCurrentWeather(apiKey, area)

    }

    fun getCurrentWeather(apiKey: String, area:String) {
        try {
            viewModelScope.launch {
               // city.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey , area).currentLocation?.city
                //state.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey , area).currentLocation?.state
                //currentWeather.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey , area).currentWeather?.currentWeatherCondition?.currentCondition
                //weatherIcon.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey , area).currentWeather?.currentWeatherCondition?.weatherIcon
                //celsius.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey , area).currentWeather?.celsius
                //fahrenheit.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey , area).currentWeather?.fahrenheit
            }
        }
        catch (e: Exception) {
            "Failure: ${e.message}"
        }
    }

}