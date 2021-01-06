package com.github.mmodzel3.lostfinder.weather

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherEndpoint {
    @GET("/data/2.5/onecall")
    suspend fun getWeatherForecast(@Query("lat") latitude: Double,
                                   @Query("lon") longitude: Double,
                                   @Query("appid") apiKey: String,
                                   @Query("units") units: String): WeatherForecast
}