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
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.android.temptext.R
import com.example.android.temptext.databinding.FragmentLandingBinding
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
 * Use the [LandingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

class LandingFragment : Fragment() {

    private lateinit var locationSearch: Button
    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null
    private var foregroundOnlyLocationServiceBound = false
    private lateinit var sharedPreferences: SharedPreferences
    private var _binding: FragmentLandingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TempTextViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate the layout for this fragment
        _binding = FragmentLandingBinding.inflate(inflater, container, false)
        locationSearch = binding.button

        return binding.root
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

       sharedPreferences = this.activity!!.getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)

        locationSearch = binding.button

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
            locationSearch.setOnClickListener { findNavController().navigate(R.id.action_landingFragment_to_mainWeatherFragment) }
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
    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            Snackbar.make(
                binding.locationTextView,
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
            ForegroundOnlyLocationService().getLastLocation(this.requireActivity())

          //  viewModel.showCurrentWeather(API_KEY,location.toString(),"yes")
        }
        val serviceIntent = Intent(activity, ForegroundOnlyLocationService::class.java)
        this.activity!!.bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }
    private fun logResultsToScreen(output: String) {
        val outputWithPreviousLogs = "$output\n${binding.locationTextView.text}"
        binding.locationTextView.text = outputWithPreviousLogs
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