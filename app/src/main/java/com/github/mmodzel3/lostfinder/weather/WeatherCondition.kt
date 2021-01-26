package com.github.mmodzel3.lostfinder.weather

import com.google.gson.annotations.SerializedName

data class WeatherCondition(val id: Int,
                            @SerializedName("icon") val iconId: String) {
}