package com.example.android.temptext.network

import com.example.android.temptext.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "https://api.weatherapi.com/v1/"
private const val API_KEY = BuildConfig.WEATHER_API_KEY
//https://github.com/square/moshi#custom-type-adapters
private val networkLoggingInterceptor =
    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .client(OkHttpClient.Builder().addInterceptor(networkLoggingInterceptor).build())
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface WeatherApiService {
    ///alerts/active/area/{area}"
    @GET("current.json?key=$API_KEY&q={area}&aqi=no")
    suspend fun getCurrentWeather(@Path("area") location: String): WeatherResponse
}

object WeatherAlertApi{
    val retrofitService: WeatherApiService by lazy {
        retrofit.create((WeatherApiService::class.java))
    }
}