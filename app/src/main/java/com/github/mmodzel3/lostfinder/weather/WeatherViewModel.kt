package com.github.mmodzel3.lostfinder.weather

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mmodzel3.lostfinder.location.Location
import kotlinx.coroutines.launch

class WeatherViewModel(private val weatherApiKey: String,
                       private val weatherUnits: String) : ViewModel() {
    companion object {
        private const val WEATHER_UPDATE_INTERVAL = 10 * 60 * 1000L
    }

    private val weatherRepository: WeatherRepository by lazy {
        WeatherRepository.getInstance(weatherApiKey, weatherUnits)
    }

    val now: MutableLiveData<Weather> = weatherRepository.now
    val nextHour: MutableLiveData<Weather> = weatherRepository.nextHour
    val today: MutableLiveData<Weather> = weatherRepository.today
    val tomorrow: MutableLiveData<Weather> = weatherRepository.tomorrow
    val weatherStatus: MutableLiveData<WeatherEndpointStatus> = MutableLiveData()

    private val handler: Handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null
    private var lastLocation: Location? = null

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
                    fetchDataAndUpdate(lastLocation!!.latitude, lastLocation!!.longitude)
                    weatherStatus.postValue(WeatherEndpointStatus.OK)
                } else {
                    weatherStatus.postValue(WeatherEndpointStatus.ERROR)
                }
            } catch (e: WeatherEndpointAccessErrorException) {
                weatherStatus.postValue(WeatherEndpointStatus.ERROR)
            }
        }
    }

    internal suspend fun fetchDataAndUpdate(latitude: Double, longitude: Double) {
        weatherRepository.fetchDataAndUpdate(latitude, longitude)
    }
}