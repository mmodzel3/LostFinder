package com.github.mmodzel3.lostfinder.weather

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mmodzel3.lostfinder.location.Location
import kotlinx.coroutines.launch

class WeatherEndpointViewModel(private val weatherEndpoint: WeatherEndpoint,
                               private val weatherApiKey: String,
                               private val weatherUnits: String) : ViewModel() {
    companion object {
        private const val SECONDS_IN_HOUR = 60 * 60
        private const val SECONDS_IN_DAY = 24 * SECONDS_IN_HOUR
        private const val WEATHER_UPDATE_INTERVAL = 10 * 60 * 1000L
    }

    val now: MutableLiveData<Weather> = MutableLiveData()
    val nextHour: MutableLiveData<Weather> = MutableLiveData()
    val today: MutableLiveData<Weather> = MutableLiveData()
    val tomorrow: MutableLiveData<Weather> = MutableLiveData()
    val weatherStatus: MutableLiveData<WeatherEndpointStatus> = MutableLiveData()

    private val handler: Handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null
    private var lastLocation: Location? = null

    fun updateWeatherLocation(location: Location) {
        lastLocation = location
    }

    fun updateWeatherLocation(latitude: Double, longitude: Double) {
        lastLocation = Location(latitude, longitude)
    }

    fun runPeriodicFetchData() {
        stopPeriodicFetchData()
        initPeriodicFetchDataTask()
        handler.post(updateRunnable!!)
    }

    fun stopPeriodicFetchData() {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable!!)
        }
    }

    protected fun initPeriodicFetchDataTask() {
        updateRunnable = Runnable {
            viewModelScope.launch {
                if (lastLocation != null) {
                    forceFetchData()
                }
                handler.postDelayed(updateRunnable!!, WEATHER_UPDATE_INTERVAL)
            }
        }
    }

    fun forceFetchData() {
        weatherStatus.postValue(WeatherEndpointStatus.FETCHING)

        viewModelScope.launch {
            try {
                if (lastLocation != null) {
                    fetchData(lastLocation!!.latitude, lastLocation!!.longitude)
                    weatherStatus.postValue(WeatherEndpointStatus.OK)
                } else {
                    weatherStatus.postValue(WeatherEndpointStatus.ERROR)
                }
            } catch (e: WeatherEndpointAccessErrorException) {
                weatherStatus.postValue(WeatherEndpointStatus.ERROR)
            }
        }
    }

    internal suspend fun fetchData(latitude: Double, longitude: Double) {
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