package com.example.android.temptext.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.android.temptext.database.TempTuple
import com.example.android.temptext.database.UserID
import com.example.android.temptext.database.WeatherDao
import com.example.android.temptext.database.WeatherModel
import com.example.android.temptext.network.NestedWeatherResponse
import com.example.android.temptext.network.WeatherAlertApi
import com.example.android.temptext.network.WeatherResponse
import kotlinx.coroutines.launch

class TempTextViewModel(val weatherDao: WeatherDao) : ViewModel() {
    private var _apiResponse = MutableLiveData<WeatherResponse>()
    val apiResponse = _apiResponse

    private var _nestedResponse = MutableLiveData<NestedWeatherResponse>()
    val nestedResponse = _nestedResponse

    private var _nestedLocateResponse = MutableLiveData<NestedWeatherResponse>()
    val nesteLocateResponse = _nestedLocateResponse

    private lateinit var uid: UserID

    private fun getCurrentWeatherResponse(apiKey: String, area: String, aqi: String) {
        uid = UserID()
        try {
            viewModelScope.launch {
                //create access for use within setupalertfragment
                val response = WeatherAlertApi.retrofitService.getCurrentWeather(
                    apiKey,
                    area,
                    aqi
                ).currentWeather
                nestedResponse.value = response

                //create access to location JSON object
                val lResponse = WeatherAlertApi.retrofitService.getCurrentWeather(
                    apiKey,
                    area,
                    aqi
                ).currentLocation
                nesteLocateResponse.value = lResponse

                //access to api response outside of class
                apiResponse.value =
                    WeatherAlertApi.retrofitService.getCurrentWeather(apiKey, area, aqi)

                addNewDataAsPrimitive(
                    response.celsius!!,
                    response.fahrenheit!!,
                    response.aqi!!.ozone.toString(),
                    lResponse.city!!,
                    response.currentWeatherCondition!!.currentCondition!!,
                    response.humidity!!,
                    response.precipitation!!,
                    uid.toString(),
                    response.wind!!
                )
            }
        } catch (e: Exception) {
            "Failure: ${e.message}"
        }
    }

    fun showCurrentWeather(apiKey: String, appArea: String, aqi: String) {
        getCurrentWeatherResponse(apiKey, appArea, aqi)
    }
//creates function to insert to database using weathermodel class objects
    fun insertIntoDatabase(userInfo: WeatherModel) {
        viewModelScope.launch { weatherDao.insertUser(userInfo) }
    }
//creates function to set variables to primitive types to use in app
    private fun setNewData(
        celsiusVm: Float, fahrenheitVm: Float, aqiVm: String,
        cityNameVm: String, currentConditionVm: String,
        humidityVm: Double, precipVm: Float, primaryKeyVm: String, windVm: Float,
    ): WeatherModel {
        return WeatherModel(
            celsius = celsiusVm,
            fahrenheit = fahrenheitVm,
            aqi = aqiVm,
            cityName = cityNameVm,
            currentWeatherCondition = currentConditionVm,
            humidity = humidityVm,
            precipitation = precipVm,
            primaryKey = primaryKeyVm,
            wind = windVm
        )
    }
//function to be used in app to insert alerts to database
    fun addNewDataAsPrimitive(
        newCelsuis: Float,
        newFahrenheit: Float,
        newAqi: String,
        newCity: String,
        newCondition: String,
        newHumidity: Double,
        newPrecipitation: Float,
        newKey: String,
        newWind: Float
    ) {
        val newData = setNewData(
            newCelsuis, newFahrenheit, newAqi, newCity, newCondition, newHumidity, newPrecipitation,
            newKey, newWind
        )
        insertIntoDatabase(newData)
    }

    fun updateDatabase(updateUser: WeatherModel) {
        viewModelScope.launch { weatherDao.updateUserInfo(updateUser) }
    }

    fun loadTemp() {
        viewModelScope.launch { weatherDao.loadTemperature() }
    }

    fun getWeatherData(tempTuple: TempTuple): Float {
        return tempTuple.fahrenheit
    }

    fun deleteUserData(userData: WeatherModel) {
        viewModelScope.launch { weatherDao.deleteUser(userData) }
    }
}

class WeatherViewModelFactory(private val weatherDao: WeatherDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TempTextViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TempTextViewModel(weatherDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}