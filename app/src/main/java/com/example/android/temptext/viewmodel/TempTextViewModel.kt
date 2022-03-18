package com.example.android.temptext.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.ViewModel
import com.example.android.temptext.BuildConfig
import com.example.android.temptext.network.WeatherAlertApi
import kotlinx.coroutines.launch

private const val API_KEY = BuildConfig.WEATHER_API_KEY

class TempTextViewModel : ViewModel(){
    private var appApiKey = API_KEY
    private var appAqi: String = "yes"
    private var appArea = "NY"

    private var _aqi = MutableLiveData<Float>()
    val aqi: LiveData<Float> = _aqi

    private var _precipitation = MutableLiveData<Float>()
    val precipitation: LiveData<Float> = _precipitation

    private var _humidity = MutableLiveData<Double>()
    val humidity: LiveData<Double> = _humidity

    private var _city = MutableLiveData<String>()
    val city: LiveData<String> = _city

    private var _state = MutableLiveData<String>()
    val state: LiveData<String> = _state

    private var _currentWeather= MutableLiveData<String>()
    val currentWeather: LiveData<String> = _currentWeather

    private var _fahrenheit = MutableLiveData<Float>()
    val fahrenheit: LiveData<Float> = _fahrenheit

    init {
        getCurrentWeather(appApiKey, appArea, appAqi)
    }
    //change area back to LiveData<String>
    private fun getCurrentWeather(apiKey: String, area: String, aqi: String) {
        try {
            viewModelScope.launch {
                _city.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey, area, aqi).currentLocation?.city!!
                _precipitation.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey, area, aqi).currentWeather?.precipitation!!
                _currentWeather.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey , area, aqi).currentWeather?.currentWeatherCondition!!.currentCondition!!
                _fahrenheit.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey, area, aqi).currentWeather?.fahrenheit!!
                _humidity.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey, area, aqi).currentWeather?.humidity!!
                _aqi.value = WeatherAlertApi.retrofitService.getCurrentWeather(apiKey, area, aqi).currentWeather?.aqi?.ozone!!
            }
        }
        catch (e: Exception) {
            "Failure: ${e.message}"
        }
    }
    fun showCurrentWeather(apiKey: String, area: String, aqi: String){
        appApiKey = apiKey
        appArea = area
        appAqi = aqi
    }
}