package com.example.android.temptext

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.android.temptext.databinding.ActivityMainBinding
import com.example.android.temptext.network.ForegroundOnlyLocationService

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_graph) as NavHostFragment
        navController = navHostFragment.navController
    }
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
    //generates BIG_TEXT_STYLE notification that represents latest location
    fun generateNotification(location: Location?): Notification {
        //get data
        val mainNotificationText = location?.toString() ?: getString(R.string.no_location_text)
        val titleText = getString(R.string.app_name)
        //create notification channel for O+ and beyond devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                ForegroundOnlyLocationService.NOTIFICATION_CHANNEL_ID, titleText, NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        //build big text style
        val bigTextStyle = NotificationCompat.BigTextStyle()
            .bigText(mainNotificationText)
            .setBigContentTitle(titleText)
        //set up main intent/pending intents for notification
        val launchActivityIntent = Intent(this, MainActivity::class.java)
 /*       val cancelIntent.putExtra(EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION , true)
        val servicePendingIntent = PendingIntent.getService(this, 0, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT)*/
        val activityPendingIntent = PendingIntent.getActivity(
            this, 0, launchActivityIntent, 0)
        //build and issue notification
        val notificationCompatBuilder = NotificationCompat.Builder(this,
            ForegroundOnlyLocationService.NOTIFICATION_CHANNEL_ID
        )
        return notificationCompatBuilder
            .setStyle(bigTextStyle)
            .setContentTitle(titleText)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setOngoing(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .addAction(
                R.drawable.ic_launch, getString(R.string.launch_activity),
                activityPendingIntent)
            .build()
    }
    companion object{
        private const val EXTRA_CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION =
            "com.example.android.temptext.MainActivity.extra.CANCEL_LOCATION_TRACKING_FROM_NOTIFICATION"

    }
}