package com.github.mmodzel3.lostfinder.weather

import com.google.gson.annotations.SerializedName

data class WeatherTemperatures(@SerializedName("day") val dayTemperature: Double)
