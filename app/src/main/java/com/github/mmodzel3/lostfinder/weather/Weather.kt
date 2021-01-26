package com.github.mmodzel3.lostfinder.weather

data class Weather(val timestamp: Long,
                   val temperature: Double,
                   val pressure: Int,
                   val humidity: Int,
                   val clouds: Int,
                   val visibility: Int,
                   val windSpeed: Double,
                   val windDegree: Double,
                   val weatherConditionList: List<WeatherCondition>) {

}
