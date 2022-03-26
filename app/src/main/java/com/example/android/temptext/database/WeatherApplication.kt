package com.example.android.temptext.database

import android.app.Application

class WeatherApplication : Application() {
    val database: ForecastDatabase by lazy { ForecastDatabase.getForecastDatabase(this) }
}