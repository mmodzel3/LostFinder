package com.github.mmodzel3.lostfinder.weather

import androidx.lifecycle.MutableLiveData

class WeatherRepository(private val weatherApiKey: String,
                        private val weatherUnits: String) {
    companion object {
        private const val SECONDS_IN_HOUR = 60 * 60
        private const val SECONDS_IN_DAY = 24 * SECONDS_IN_HOUR

        private var weatherRepository: WeatherRepository? = null

        fun getInstance(weatherApiKey: String,
                        weatherUnits: String): WeatherRepository {
            if (weatherRepository == null) {
                weatherRepository = WeatherRepository(weatherApiKey, weatherUnits)
            }

            return weatherRepository!!
        }

        fun clear() {
            weatherRepository = null
        }
    }

    val now: MutableLiveData<Weather> = MutableLiveData()
    val nextHour: MutableLiveData<Weather> = MutableLiveData()
    val today: MutableLiveData<Weather> = MutableLiveData()
    val tomorrow: MutableLiveData<Weather> = MutableLiveData()

    private val weatherEndpoint: WeatherEndpoint by lazy {
        WeatherEndpointFactory.createWeatherEndpoint()
    }

    internal suspend fun fetchDataAndUpdate(latitude: Double, longitude: Double) {
        val weatherForecast: WeatherForecast = weatherEndpoint.getWeatherForecast(latitude, longitude,
                weatherApiKey, weatherUnits)

        val currentTimestamp: Long = System.currentTimeMillis() / 1000
        val nextHourTimestamp: Long = currentTimestamp - (currentTimestamp % SECONDS_IN_HOUR) + SECONDS_IN_HOUR
        val todayTimestamp: Long = currentTimestamp - (currentTimestamp % SECONDS_IN_DAY)
        val tomorrowTimestamp: Long = todayTimestamp + SECONDS_IN_DAY

        val hourlyWeatherForecast = weatherForecast.hourly.filter { it.timestamp >= nextHourTimestamp }
        val dailyWeatherForecast = weatherForecast.daily.filter { it.timestamp >= tomorrowTimestamp }
        val previousDailyWeatherForecast = weatherForecast.daily.filter { it.timestamp in todayTimestamp until tomorrowTimestamp }

        hourlyWeatherForecast.sortedBy { it.timestamp }
        dailyWeatherForecast.sortedBy { it.timestamp }
        previousDailyWeatherForecast.sortedBy { it.timestamp }

        now.postValue(weatherForecast.now.convertToWeather())

        if (hourlyWeatherForecast.isNotEmpty()) {
            nextHour.postValue(hourlyWeatherForecast[0].convertToWeather())
        }

        if (previousDailyWeatherForecast.isNotEmpty()) {
            today.postValue(previousDailyWeatherForecast[0].convertToWeather())
        }

        if (dailyWeatherForecast.isNotEmpty()) {
            tomorrow.postValue(dailyWeatherForecast[0].convertToWeather())
        }
    }
}