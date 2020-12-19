package com.github.mmodzel3.lostfinder.weather

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object WeatherEndpointFactory {
    var WEATHER_API_URL = "https://api.openweathermap.org/"

    fun createWeatherEndpoint() : WeatherEndpoint {
        val client: OkHttpClient = OkHttpClient.Builder()
                                    .addInterceptor(WeatherEndpointErrorInterceptor())
                                    .build()

        return Retrofit.Builder()
                .baseUrl(WEATHER_API_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherEndpoint::class.java)
    }
}