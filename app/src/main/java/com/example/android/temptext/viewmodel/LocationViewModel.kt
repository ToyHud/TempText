package com.example.android.temptext.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationRequest

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationLiveData = LocationLiveData(application)

    fun getLocationLiveData()= locationLiveData
}

class LocationLiveData(application: Application) : LiveData<LocationDetails>() {
    companion object{
        val locationRequest: LocationRequest = LocationRequest.create()
    }
}

data class LocationDetails (val longitude: String, val latitude: String)
