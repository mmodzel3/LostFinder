package com.github.mmodzel3.lostfinder.weather

import com.google.gson.annotations.SerializedName

data class WeatherDay(@SerializedName("dt") val timestamp: Long,
                      @SerializedName("temp") val temperatures: WeatherTemperatures,
                      val pressure: Int,
                      val humidity: Int,
                      val clouds: Int,
                      val visibility: Int,
                      @SerializedName("wind_speed") val windSpeed: Double,
                      @SerializedName("wind_deg") val windDegree: Double,
                      @SerializedName("weather") val weatherConditionList: List<WeatherCondition>)
    : WeatherDataInterface {

    override fun convertToWeather() : Weather {
        return Weather(timestamp, temperatures.dayTemperature, pressure, humidity, clouds, visibility, windSpeed,
                windDegree, weatherConditionList)
    }
}
