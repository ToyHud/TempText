package com.example.android.temptext.ui


import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.example.android.temptext.R
import com.example.android.temptext.databinding.FragmentMainWeatherBinding
import com.example.android.temptext.network.ForegroundOnlyLocationService
import com.example.android.temptext.network.ForegroundOnlyLocationService.Companion.API_KEY
import com.example.android.temptext.network.SharedPreferenceUtil
import com.example.android.temptext.network.toText
import com.example.android.temptext.viewmodel.TempTextViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.snackbar.Snackbar

/**
 * A simple [Fragment] subclass.
 * Used to implement methods for fragment_main_weather
 */
class MainWeatherFragment : Fragment() {

    private lateinit var statusTextView: TextView
    private lateinit var degreeTextView: TextView
    private lateinit var alertsButton: Button
    private lateinit var precipTextView: TextView
    private lateinit var aqiTextView: TextView
    private lateinit var windTextView: TextView
    private val viewModel: TempTextViewModel by activityViewModels()
    private lateinit var cityTextView: TextView
    private var _binding: FragmentMainWeatherBinding? = null
    private val binding get() = _binding!!
    private lateinit var locationTextView: SearchView
    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver
    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null
    private var foregroundOnlyLocationServiceBound = false
    private lateinit var locationSearch: Button
    private lateinit var sharedPreferences: SharedPreferences
    /**
     * Provides the entry point to the Fused Location Provider API.
     * FusedLocationProviderClient - Main class for receiving location updates
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val cancellationTokenSource = CancellationTokenSource()

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
        //set variables for xml views for cleaner code throughout
        statusTextView = binding.weatherStatus
        degreeTextView = binding.currentTemp
        alertsButton = binding.notiBtn
        precipTextView = binding.rainChance
        aqiTextView = binding.airQuality
        windTextView = binding.wind
        locationTextView = binding.searchView
        cityTextView = binding.cityView

        alertsButton.setOnClickListener { findNavController().navigate(R.id.action_mainWeatherFragment_to_setUpAlertFragment) }
        //allows search view to take zip code or city/state and add as paramater for api url
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

        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

        sharedPreferences = this.activity!!.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        locationSearch = binding.buttonShareLocation

        locationSearch.setOnClickListener {
            val enabled = sharedPreferences.getBoolean(
                SharedPreferenceUtil.KEY_FOREGROUND_ENABLED, false)

            if (enabled) {
                foregroundOnlyLocationService?.unsubscribeToLocationUpdates()
            } else {

                // TODO: Step 1.0, Review Permissions: Checks and requests if needed.
                if (foregroundPermissionApproved()) {
                    foregroundOnlyLocationService?.subscribeToLocationUpdates()
                        ?: Log.d("MainAct", "Service Not Bound")
                } else {
                    requestForegroundPermissions()
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this.requireContext()).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        )
    }
    //sets api call data to views
    private fun displayWeather() {
        viewModel.apiResponse.observe(this, {
            val responseArray = arrayListOf(it.currentLocation, it.currentWeather)
            val currentWeather = it.currentWeather.currentWeatherCondition!!.currentCondition!!
            val city = it.currentLocation.city!!
            val airQuality = it.currentWeather.aqi!!.ozone!!
            Log.d("FragDisplayWeather", currentWeather)
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
                Log.d("MainCity", city)
            }
        })
    }
    //sets location data pulled from fused location provider as parameter for api url
    @SuppressLint("MissingPermission")
    fun getLastLocation(activity: Activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationClient.lastLocation
            .addOnCompleteListener { location ->
                Log.d("ForegroundLocate1", "$location")
                Log.d("ForegroundLocateResult", "${location.result}")
                if (location.isSuccessful && location.result != null) {
                    val latitude = location.result.latitude
                    val longitude = location.result.longitude
                    val currentLocation = """$latitude,$longitude"""
                    viewModel.showCurrentWeather(API_KEY, currentLocation, "yes")
                    Log.d("MainLocate", currentLocation)
                } else {
                    MainWeatherFragment().requestForegroundPermissions()
                    Log.d(
                        "ForeGroundError",
                        "getLastLocation:exception",
                        location.exception
                    )
                    cancellationTokenSource.cancel()
                }
            }
    }
    // Monitors connection to the while-in-use service.
    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ForegroundOnlyLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }
    //Method checks if permissions approved.
    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }
    //Method requests permissions.
    fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            Snackbar.make(
                binding.errorTextView,
                R.string.permission_rationale,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.ok) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this.requireActivity(),
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()
        } else {
            Log.d("MainActPermission", "Request foreground only permission")
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
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
           getLastLocation(this.requireActivity())
        }
        val serviceIntent = Intent(activity, ForegroundOnlyLocationService::class.java)
        this.activity!!.bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }
    private fun logResultsToScreen(output: String) {
        val outputWithPreviousLogs = "$output\n${binding.errorTextView.text}"
        binding.errorTextView.text = outputWithPreviousLogs
    }
    /**
     * Receiver for location broadcasts from [ForegroundOnlyLocationService].
     */
    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                ForegroundOnlyLocationService.EXTRA_LOCATION
            )

            if (location != null) {
                logResultsToScreen("Foreground location: ${location.toText()}")
            }
        }
    }
}
