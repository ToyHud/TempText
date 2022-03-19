package com.example.android.temptext.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.android.temptext.BuildConfig
import com.example.android.temptext.R
import com.example.android.temptext.databinding.FragmentMainWeatherBinding
import com.example.android.temptext.network.ForegroundOnlyLocationService
import com.example.android.temptext.viewmodel.TempTextViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource

/**
 * A simple [Fragment] subclass.
 * Used to implement methods for fragment_main_weather
 */
private const val API_KEY = BuildConfig.WEATHER_API_KEY

class MainWeatherFragment : Fragment() {

    private lateinit var statusTextView: TextView
    private lateinit var degreeTextView: TextView
    private lateinit var alertsButton: Button
    private lateinit var precipTextView: TextView
    private lateinit var aqiTextView: TextView
    private lateinit var windTextView: TextView
    private val viewModel: TempTextViewModel by activityViewModels()
    private val cancellationTokenSource = CancellationTokenSource()
    private lateinit var cityTextView: TextView
    /**
     * Provides the entry point to the Fused Location Provider API.
     * FusedLocationProviderClient - Main class for receiving location updates.
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentMainWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationTextView: SearchView

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
        precipTextView = binding.rainChance
        aqiTextView = binding.airQuality
        windTextView = binding.wind
        locationTextView = binding.searchView
        cityTextView = binding.cityView

        alertsButton.setOnClickListener { findNavController().navigate(R.id.action_mainWeatherFragment_to_setUpAlertFragment) }

        locationTextView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                locationTextView.clearFocus()
                viewModel.showCurrentWeather(API_KEY, query, "yes")
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun displayWeather() {
        viewModel.apiResponse.observe(this, {
            val responseArray = arrayListOf(it.currentLocation, it.currentWeather)
            val currentWeather = it.currentWeather.currentWeatherCondition!!.currentCondition!!
            val city = it.currentLocation.city!!
            val airQuality = it.currentWeather.aqi!!.ozone!!

            if (airQuality <= 50) {
                aqiTextView.text = resources.getString(R.string.good)
            } else if (airQuality >= 51 || airQuality <= 100) {
                aqiTextView.text = resources.getString(R.string.moderate)
            } else if (airQuality >= 101 || airQuality <= 150) {
                aqiTextView.text = resources.getString(R.string.unhealthy_sensitive)
            } else if (airQuality >= 151 || airQuality <= 200) {
                aqiTextView.text = resources.getString(R.string.unhealthy)
            } else if (airQuality >= 201 || airQuality <= 300) {
                aqiTextView.text = resources.getString(R.string.very_unhealthy)
            } else if (airQuality >= 301) {
                aqiTextView.text = resources.getString(R.string.hazardous)
            }
            for (response in responseArray) {
                degreeTextView.text = response.fahrenheit.toString()
                precipTextView.text = response.precipitation.toString()
                windTextView.text = response.wind.toString()
                statusTextView.text = currentWeather
                cityTextView.text = city
            }
        })
    }
    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this.requireActivity())
        fusedLocationClient.lastLocation
            .addOnCompleteListener { location ->
                if (location.isSuccessful && location.result != null) {
                    val latitude = location.result.latitude
                    val longitude = location.result.longitude
                    val currentLocation = "$latitude , $longitude"
                    viewModel.showCurrentWeather(API_KEY, currentLocation, "yes")
                } else {
                    Log.d(
                        "ForeGroundError",
                        "getLastLocation:exception",
                        location.exception
                    )
                    cancellationTokenSource.cancel()
                }
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