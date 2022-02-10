package com.example.android.temptext

import android.Manifest
import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.annotation.SuppressLint
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.android.temptext.network.FusedLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource

//private const val API_KEY = BuildConfig.WEATHER_API_KEY

class MainActivity : AppCompatActivity() {
    /**
     * Provides the entry point to the Fused Location Provider API.
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
         * Instantiates Fused Location Provider
         */
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        /*
        val city = MutableLiveData<String>()
        val state = MutableLiveData<String>()
        val currentWeather = MutableLiveData<String>()
        val weatherIcon = MutableLiveData<String>()
        val celsius = MutableLiveData<Float>()
        val fahrenheit = MutableLiveData<Float>()

        try {
            lifecycleScope.launch {
                city.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY","no").currentLocation?.city
                state.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY","no").currentLocation?.state
                currentWeather.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY","no").currentWeather?.currentWeatherCondition?.currentCondition
                weatherIcon.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY","no").currentWeather?.currentWeatherCondition?.weatherIcon
                celsius.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY","no").currentWeather?.celsius
                fahrenheit.value = WeatherAlertApi.retrofitService.getCurrentWeather(API_KEY,"NY","no").currentWeather?.fahrenheit

                Log.d("MainActivityCity",city.value.toString())
                Log.d("MainActivityCity",state.value.toString())
                Log.d("MainActivityWeather", currentWeather.value.toString())
                Log.d("MainActivityIcon", weatherIcon.value.toString())
                Log.d("MainActivityCel", celsius.value.toString())
                Log.d("MainActivityFahr", fahrenheit.value.toString())
            }
        } catch (e: Exception) {
            "Failure: ${e.message}"
        }*/
    }

    override fun onStart() {
        super.onStart()
        /* *
         * Check permissions when activity starts
         * */

        val fusedLocation = FusedLocation()
        if (!fusedLocation.checkPermissions()) {
            fusedLocation.requestPermissions()
        } else {
            fusedLocation.getLastLocation()
        }
    }

}

