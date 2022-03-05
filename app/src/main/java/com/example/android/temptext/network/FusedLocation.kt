package com.example.android.temptext.network

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.CancellationTokenSource

class FusedLocation {
    private val cancellationTokenSource = CancellationTokenSource()
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private val TAG = "MainActivity"
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var application: Application
    private lateinit var activity: Activity
    /* *
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     *
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressLint("MissingPermission")
    fun getLastLocation() {
        fusedLocationClient.lastLocation
            .addOnCompleteListener { location ->
                if (location.isSuccessful && location.result != null) {
                    val location = location.result
                    println("$location")

                    Log.d("MainAct", "$location")
                } else {
                    Log.d("MainActError", "getLastLocation:exception", location.exception)
                }
                cancellationTokenSource.cancel()
            }
    }
    /**
     * Return the current state of the permissions needed.
     */
    fun checkPermissions() =
        ActivityCompat.checkSelfPermission(
            application.applicationContext,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            activity.parent, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }
    fun requestPermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity.parent,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            // Request permission
            startLocationPermissionRequest()

        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest()
        }
    }
}