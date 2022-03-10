package com.example.android.temptext.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import com.example.android.temptext.BuildConfig
import com.example.android.temptext.R
import com.example.android.temptext.databinding.FragmentMainWeatherBinding
import com.example.android.temptext.network.ForegroundOnlyLocationService
import com.example.android.temptext.viewmodel.TempTextViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/**
 * A simple [Fragment] subclass.
 * Use the [MainWeatherFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val API_KEY = BuildConfig.WEATHER_API_KEY

class MainWeatherFragment : Fragment() {

    private lateinit var statusTextView: TextView
    private lateinit var degreeTextView: TextView
    private lateinit var alertsButton: Button
    private lateinit var precipTextView: TextView
    private lateinit var aqiTextView: TextView
    private lateinit var humidTextView: TextView
    private val viewModel: TempTextViewModel by activityViewModels()
    private val cancellationTokenSource = CancellationTokenSource()
    /**
     * Provides the entry point to the Fused Location Provider API.
     * FusedLocationProviderClient - Main class for receiving location updates.
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding : FragmentMainWeatherBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        displayWeather()
        // Inflate the layout for this fragment
        _binding = FragmentMainWeatherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statusTextView = binding.weatherStatus
        degreeTextView = binding.currentTemp
        alertsButton = binding.notiBtn
        precipTextView  = binding.rainChance
        aqiTextView = binding.airQuality
        humidTextView = binding.humidity
    }
    private fun displayWeather() {
         viewModel.fahrenheit.observe(this, { _ ->
             degreeTextView.text = floor(viewModel.fahrenheit.value!!).toString()
         })
         viewModel.currentWeather.observe(this, {
             statusTextView.text = viewModel.currentWeather.value!!
         })
        viewModel.aqi.observe(this,{
            val airQuality = viewModel.aqi.value!!
            if (airQuality <= 50 ) {
                aqiTextView.text = resources.getString(R.string.good)
            }else if (airQuality >= 51 || airQuality <= 100){
                aqiTextView.text = resources.getString(R.string.moderate)
            }else if (airQuality >= 101 || airQuality <= 150){
                aqiTextView.text = resources.getString(R.string.unhealthy_sensitive)
            }else if (airQuality >= 151 || airQuality <= 200){
                aqiTextView.text = resources.getString(R.string.unhealthy)
            }else if (airQuality >= 201 || airQuality <= 300){
                aqiTextView.text = resources.getString(R.string.very_unhealthy)
            }else if (airQuality >= 301){
                aqiTextView.text = resources.getString(R.string.hazardous)
            }
        })
        viewModel.humidity.observe(this, {
            humidTextView.text = floor(viewModel.humidity.value!!).toString()
        })
        viewModel.precipitation.observe(this,{
            precipTextView.text = floor(viewModel.precipitation.value!!).toString()
        })
     }
    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        fusedLocationClient.lastLocation
            .addOnCompleteListener { location ->
                if (location.isSuccessful && location.result != null) {
                    val latitude = location.result.latitude
                    val longitude = location.result.longitude
                    val currentLocation = "$latitude,$longitude"
                    viewModel.showCurrentWeather(API_KEY, currentLocation, "yes")
                } else {
                    Log.d("ForeGroundError", "getLastLocation:exception", location.exception)
                }
                cancellationTokenSource.cancel()
            }
    }
    override fun onStart() {
        super.onStart()
        /* *
         * Check permissions when activity starts
         * */
        val fusedLocation = ForegroundOnlyLocationService()
        if (!fusedLocation.checkPermissions(this.requireContext())) {
            fusedLocation.requestPermissions(this.requireActivity())
        } else {
            getLastLocation()
        }
    }
}