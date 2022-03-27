package com.example.android.temptext

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.android.temptext.databinding.ActivityMainBinding
import com.example.android.temptext.network.ForegroundOnlyLocationService.Companion.NOTIFICATION_CHANNEL_ID

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var binding: ActivityMainBinding

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
    fun notification (){
       val textTitle = "alert"
       val textContent = "weather alerts"
           var builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
       .setSmallIcon(R.drawable.notification_icon)
       .setContentTitle(textTitle)
       .setContentText(textContent)
       .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance)
        }
    }

}
