package com.example.android.temptext

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.android.temptext.network.ForegroundOnlyLocationService
import com.example.android.temptext.viewmodel.TempTextViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.math.absoluteValue
import kotlin.math.roundToInt
private const val API_KEY = BuildConfig.WEATHER_API_KEY
class MainActivity : AppCompatActivity() {

    private val viewModel: TempTextViewModel by viewModels()
    private val cancellationTokenSource = CancellationTokenSource()
    private lateinit var locationTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var degreeTextView: TextView
    private lateinit var alertsButton: Button

    /**
     * Provides the entry point to the Fused Location Provider API.
     * FusedLocationProviderClient - Main class for receiving location updates.
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationTextView = findViewById(R.id.location)
        statusTextView = findViewById(R.id.status)
        degreeTextView = findViewById(R.id.temp)
        alertsButton = findViewById(R.id.button)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        displayWeather()

        //WeatherAlertApi.retrofitService.getCurrentWeather(apiKey , area, aqi).currentLocation?.state!!
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation
            .addOnCompleteListener { location ->
                if (location.isSuccessful && location.result != null) {
                    val latitude = location.result.latitude
                    val longitude = location.result.longitude
                    val currentLocation = "$latitude,$longitude"
                    viewModel.showCurrentWeather(API_KEY, currentLocation,"yes")
                } else {
                    Log.d("ForeGroundError", "getLastLocation:exception", location.exception)
                }
                cancellationTokenSource.cancel()
            }
    }

    private fun displayWeather() {
        viewModel.fahrenheit.observe(this, { _ ->
            degreeTextView.text = viewModel.fahrenheit.value!!.roundToInt().toString()
        })
        viewModel.currentWeather.observe(this, {
            statusTextView.text = viewModel.currentWeather.value!!
        })
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
            getLastLocation()
        }
    }
}
