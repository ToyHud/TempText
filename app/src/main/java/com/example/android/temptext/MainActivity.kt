package com.example.android.temptext

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import com.example.android.temptext.viewmodel.TempTextViewModel
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.android.temptext.network.ForegroundOnlyLocationService
import com.example.android.temptext.network.WeatherAlertApi
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

private const val API_KEY = BuildConfig.WEATHER_API_KEY

class MainActivity : AppCompatActivity() {

    private val viewModel: TempTextViewModel by viewModels()

    /**
     * Provides the entry point to the Fused Location Provider API.
     * FusedLocationProviderClient - Main class for receiving location updates.
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
         * Instantiates Fused Location Provider
         */
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val city = MutableLiveData<String>()
        val state = MutableLiveData<String>()
        val currentWeather = MutableLiveData<String>()
        val celsius = MutableLiveData<Float>()
        val fahrenheit = MutableLiveData<Float>()
        val humidity = MutableLiveData<String>()
        val dayOfWeek = MutableLiveData<Int>()
        val windMph = MutableLiveData<Float>()
        val precipitation = MutableLiveData<Float>()
        val carbonMonoxide = MutableLiveData<Float>()

        try {
            lifecycleScope.launch {
                city.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY","no").currentLocation?.city!!
                state.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY","no").currentLocation?.state!!
                currentWeather.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY","no").currentWeather?.currentWeatherCondition?.currentCondition!!
                celsius.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY","no").currentWeather?.celsius!!
                fahrenheit.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY","no").currentWeather?.fahrenheit!!
                city.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY", "yes").currentLocation?.city!!
                state.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY", "yes").currentLocation?.state!!
                currentWeather.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY", "yes").currentWeather?.currentWeatherCondition?.currentCondition!!
                celsius.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY", "yes").currentWeather?.celsius!!
                fahrenheit.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY", "yes").currentWeather?.fahrenheit!!
                humidity.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY", "yes").currentWeather?.humidity!!
                dayOfWeek.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY", "yes").currentWeather?.dayOfWeek!!
                windMph.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY", "yes").currentWeather?.windMph!!
                precipitation.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY", "yes").currentWeather?.precipitation!!
//                carbonMonoxide.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY", "yes").currentWeather?.aqi?.carbonMonoxide!!

                Log.d("MainActivityCity",city.value.toString())
                Log.d("MainActivityRegion",state.value.toString())
                Log.d("MainActivityWeather", currentWeather.value.toString())
                Log.d("MainActivityCel", celsius.value.toString())
                Log.d("MainActivityFahr", fahrenheit.value.toString())
                Log.d("MainActivityHumid", humidity.value.toString())
                Log.d("MainActivityCO", carbonMonoxide.value.toString())
            }
        } catch (e: Exception) {
            "Failure: ${e.message}"
        }
    }

    override fun onStart() {
        super.onStart()
        /* *
         * Check permissions when activity starts
         * */

        val fusedLocation = ForegroundOnlyLocationService()
        if (!fusedLocation.checkPermissions(this)) {
            fusedLocation.requestPermissions(this)
        } else {
            fusedLocation.getLastLocation(this)
        }
    }
}
