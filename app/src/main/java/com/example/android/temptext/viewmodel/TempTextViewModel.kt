package com.example.android.temptext.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.android.temptext.BuildConfig
import com.example.android.temptext.network.WeatherAlertApi
import com.example.android.temptext.network.WeatherResponse
import kotlinx.coroutines.launch

private const val API_KEY = BuildConfig.WEATHER_API_KEY

class TempTextViewModel : ViewModel(){
    private var appApiKey = API_KEY
    private var appArea = "NY"

    private var _apiResponse = MutableLiveData<WeatherResponse>()
    val apiResponse = _apiResponse

  /*  private var _aqi = MutableLiveData<Float>()
    val aqi: LiveData<Float> = _aqi

    private var _precipitation = MutableLiveData<Float>()
    val precipitation: LiveData<Float> = _precipitation

    private var _wind = MutableLiveData<Float>()
    val wind: LiveData<Float> = _wind

    private var _city = MutableLiveData<String>()
    val city: LiveData<String> = _city

    private var _state = MutableLiveData<String>()
    val state: LiveData<String> = _state

    private var _currentWeather= MutableLiveData<String>()
    val currentWeather: LiveData<String> = _currentWeather

    private var _fahrenheit = MutableLiveData<Float>()
    val fahrenheit: LiveData<Float> = _fahrenheit*/

    init {
        getCurrentWeatherResponse(appApiKey, appArea)
    }
    //change area back to LiveData<String>
    private fun getCurrentWeatherResponse(apiKey: String, area: String) {
        try {
            viewModelScope.launch {
               apiResponse.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey, area)
            }
        }
        catch (e: Exception) {
            "Failure: ${e.message}"
        }
    }
    fun showCurrentWeather(apiKey: String, area: String){
        appApiKey = apiKey
        appArea = area
    }
}