package com.example.android.temptext.ui


import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
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
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import androidx.room.Room
import com.example.android.temptext.MainActivity
import com.example.android.temptext.R
import com.example.android.temptext.database.ForecastDatabase
import com.example.android.temptext.databinding.FragmentMainWeatherBinding
import com.example.android.temptext.network.ForegroundOnlyLocationService
import com.example.android.temptext.network.ForegroundOnlyLocationService.Companion.API_KEY
import com.example.android.temptext.network.ForegroundOnlyLocationService.Companion.EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION
import com.example.android.temptext.network.ForegroundOnlyLocationService.Companion.REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
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
//    private val weatherArgs: MainWeatherFragmentArgs by navArgs()
    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver
    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null
    private var foregroundOnlyLocationServiceBound = false

    /**
     * Provides the entry point to the Fused Location Provider API.
     * FusedLocationProviderClient - Main class for receiving location updates
     */
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val cancellationTokenSource = CancellationTokenSource()
    private lateinit var notificationManager: NotificationManager

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
        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

        setupAlerts()
        searchLocation()
    }
    private fun setupAlerts() {
/*        val weatherData = weatherArgs.weatherDataPassed
        degreeTextView.text = weatherData
        val action = MainWeatherFragmentDirections.actionMainWeatherFragmentToSetUpAlertFragment(weatherData)
        Log.d("MainWeather", weatherData)*/
        alertsButton.setOnClickListener { findNavController().navigate(R.id.action_mainWeatherFragment_to_setUpAlertFragment) }

        val db = Room.databaseBuilder(
            this.requireContext(),
            ForecastDatabase::class.java, "userInfo"
        ).build()
        db.weatherDao()
    }
    private fun searchLocation(){
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
    }
    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this.requireContext()).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST
            )
        )
    }

    //sets api call data to views
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

    //sets location data pulled from fused location provider as parameter for api url
    @SuppressLint("MissingPermission")
    fun getLastLocation(activity: Activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationClient.lastLocation
            .addOnCompleteListener { location ->
                if (location.isSuccessful && location.result != null) {
                    val latitude = location.result.latitude
                    val longitude = location.result.longitude
                    val currentLocation = """$latitude,$longitude"""
                    viewModel.showCurrentWeather(API_KEY, currentLocation, "yes")
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

    /*
    * Generates a BIG_TEXT_STYLE Notification that represent latest location.
    */
    fun generateNotification(location: Location?): Notification {
        Log.d("MainWeatFrag", "generateNotification()")

        // Main steps for building a BIG_TEXT_STYLE notification:
        //      0. Get data
        //      1. Create Notification Channel for O+
        //      2. Build the BIG_TEXT_STYLE
        //      3. Set up Intent / Pending Intent for notification
        //      4. Build and issue the notification

        // 0. Get data
        val mainNotificationText = location?.toText() ?: getString(R.string.no_location_text)
        val titleText = getString(R.string.app_name)

        // 1. Create Notification Channel for O+ and beyond devices (26+).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationChannel = NotificationChannel(
                ForegroundOnlyLocationService.NOTIFICATION_CHANNEL_ID,
                titleText,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            // Adds NotificationChannel to system. Attempting to create an
            // existing notification channel with its original values performs
            // no operation, so it's safe to perform the below sequence.
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // 2. Build the BIG_TEXT_STYLE.
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)

        // 3. Set up main Intent/Pending Intents for notification.
        val launchActivityIntent = Intent(this.requireActivity(), MainActivity::class.java)

        val cancelIntent = Intent(this.requireContext(), ForegroundOnlyLocationService::class.java)
        cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION, true)

        val servicePendingIntent = PendingIntent.getService(
            this.requireContext(), 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val activityPendingIntent = PendingIntent.getActivity(
            this.requireContext(), 0, launchActivityIntent, 0
        )

        // 4. Build and issue the notification.
        // Notification Channel Id is ignored for Android pre O (26).
        val notificationCompatBuilder =
            NotificationCompat.Builder(
                this.requireContext(),
                ForegroundOnlyLocationService.NOTIFICATION_CHANNEL_ID
            )

        return notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentTitle(titleText)
            .setContentText(mainNotificationText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.ic_launch, getString(R.string.launch_activity),
                activityPendingIntent
            )
            .addAction(
                R.drawable.ic_cancel,
                getString(R.string.stop_location_updates_button_text),
                servicePendingIntent
            )
            .build()
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
        this.activity!!.bindService(
            serviceIntent,
            foregroundOnlyServiceConnection,
            Context.BIND_AUTO_CREATE
        )
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
