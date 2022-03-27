package com.example.android.temptext.network

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.provider.ContactsContract.Directory.PACKAGE_NAME
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.android.temptext.BuildConfig
import com.example.android.temptext.ui.MainWeatherFragment
import com.google.android.gms.location.*
import java.util.concurrent.TimeUnit

class ForegroundOnlyLocationService : Service() {
    /*
     * Checks whether the bound activity has really gone away (foreground service with notification
    * created) or simply orientation change.
    */
    private var configurationChange = false
    private var serviceRunningInForeground = false
    private val localBinder = LocalBinder()
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null
    private lateinit var notificationManager: NotificationManager
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private val TAG = "ForegroundLocation"

    /**
     * Provides the entry point to the Fused Location Provider API.
     * FusedLocationProviderClient - Main class for receiving location updates
     */
   private lateinit var fusedLocationClient: FusedLocationProviderClient
 /*    private val cancellationTokenSource = CancellationTokenSource()*/

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        //create location request
        locationRequest = LocationRequest.create().apply {
            // Sets the desired interval for active location updates.
            interval = TimeUnit.SECONDS.toMillis(60)
            // Sets the fastest rate for active location updates.
            fastestInterval = TimeUnit.SECONDS.toMillis(30)
            // Sets the maximum time when batched location updates are delivered.
            maxWaitTime = TimeUnit.MINUTES.toMillis(2)

            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        /*Initialize the LocationCallback. callback that fusedLocationProvider will call when
        * new location update is available
        */
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                /* Normally, you want to save a new location to a database. simplifying
                 things a bit and just saving it as a local variable, as we only need it again
                 if a Notification is created (when the user navigates away from app).*/
                //get the latest location and save to database
                currentLocation = locationResult.lastLocation
                val intent = Intent()
                /* Notify our Activity that a new location was added either using local broadcast
                 * or service running as forground service
                 */
                if (serviceRunningInForeground) {
                    notificationManager.notify(
                        NOTIFICATION_ID,
                        MainWeatherFragment().generateNotification(currentLocation)
                    )
                }
            }
        }
    }


    fun subscribeToLocationUpdates() {
        Log.d(TAG, "subscribeToLocationUpdates()")

        SharedPreferenceUtil.saveLocationTrackingPref(this, true)

        // Binding to this service doesn't actually trigger onStartCommand(). That is needed to
        // ensure this Service can be promoted to a foreground service, i.e., the service needs to
        // be officially started (which we do here).
        startService(Intent(applicationContext, ForegroundOnlyLocationService::class.java))

        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest, locationCallback, Looper.getMainLooper()
            )
        } catch (unlikely: SecurityException) {
            SharedPreferenceUtil.saveLocationTrackingPref(this, false)
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    fun unsubscribeToLocationUpdates() {
        Log.d(TAG, "unsubscribeToLocationUpdates()")
        try {
            //unsubscribe from location callback
            val removeTask = fusedLocationClient.removeLocationUpdates(locationCallback)
            removeTask.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Location Callback removed")
                    stopSelf()
                } else {
                    Log.d(TAG, "Failed to Remove Callback")
                }
            }
            SharedPreferenceUtil.saveLocationTrackingPref(this, false)

        } catch (unlikely: SecurityException) {
            SharedPreferenceUtil.saveLocationTrackingPref(this, true)
            Log.e(TAG, "Lost location permissions. Couldn't remove updates. $unlikely")
        }
    }

    /**
     * Return the current state of the permissions needed.
     */
    fun checkPermissions(context: Context) =
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    fun startLocationPermissionRequest(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    fun requestPermissions(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            // Provide an additional rationale to the user. This would happen if the user denied the
            // request previously, but didn't check the "Don't ask again" checkbox.
            Log.i(TAG, "Displaying permission rationale to provide additional context.")
            // Request permission
            startLocationPermissionRequest(activity)

        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            Log.i(TAG, "Requesting permission")
            startLocationPermissionRequest(activity)
        }
    }

    override fun onBind(intent: Intent?): IBinder {
        // MainActivity (client) comes into foreground and binds to service, so the service can
        // become a background services.
        stopForeground(true)
        serviceRunningInForeground = false
        configurationChange = false
        return localBinder
    }

    /**
     * Class used for the client Binder.  Since this service runs in the same process as its
     * clients, we don't need to deal with IPC.
     */
    inner class LocalBinder : Binder() {
        internal val service: ForegroundOnlyLocationService
            get() = this@ForegroundOnlyLocationService
    }

    companion object {
        private const val NOTIFICATION_ID = 12345678
        internal const val NOTIFICATION_CHANNEL_ID = "while_in_use_channel_01"
        internal const val EXTRA_LOCATION = "$PACKAGE_NAME.extra.LOCATION"
        internal const val API_KEY = BuildConfig.WEATHER_API_KEY
        internal const val ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST =
            "$PACKAGE_NAME.action.FOREGROUND_ONLY_LOCATION_BROADCAST"
        internal const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
        internal const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            "$PACKAGE_NAME.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"
    }
}