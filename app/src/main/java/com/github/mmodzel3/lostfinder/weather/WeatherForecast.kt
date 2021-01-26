package com.github.mmodzel3.lostfinder.weather

import com.google.gson.annotations.SerializedName

data class WeatherForecast(@SerializedName("current") val now: WeatherCurrent,
                           val hourly: List<WeatherHour>,
                           val daily: List<WeatherDay>) {
}
