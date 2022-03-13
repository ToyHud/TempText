package com.example.android.temptext.network

import com.squareup.moshi.Json

class WeatherResponse (
    @Json(name = "location") val currentLocation: NestedWeatherResponse?,
    @Json(name = "current") val currentWeather: NestedWeatherResponse?,
)

class NestedWeatherResponse (
    @Json(name = "temp_c") val celsius: Float?,
    @Json(name = "temp_f") val fahrenheit: Float?,
    @Json(name = "name") val city: String?,
    @Json(name = "condition") val currentWeatherCondition: WeatherConditions?,
    @Json(name = "precip_in") val precipitation: Float?,
    @Json(name = "humidity") val humidity: Double?,
    @Json(name = "air_quality") val aqi: WeatherConditions?,
    @Json(name = "wind_mph") val wind: Float?
)

class WeatherConditions (
    @Json(name = "text") val currentCondition: String?,
    @Json(name = "co") val ozone: Float?,
)