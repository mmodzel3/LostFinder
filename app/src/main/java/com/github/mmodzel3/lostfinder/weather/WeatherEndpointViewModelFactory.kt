package com.github.mmodzel3.lostfinder.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WeatherEndpointViewModelFactory(private val weatherEndpoint: WeatherEndpoint,
                                      private val weatherApiKey: String,
                                      private val weatherUnits: String) : ViewModelProvider.Factory {
    companion object {
        private var weatherEndpointViewModel: WeatherEndpointViewModel? = null
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (weatherEndpointViewModel == null) {
            weatherEndpointViewModel = WeatherEndpointViewModel(weatherEndpoint, weatherApiKey, weatherUnits)
        }

        return weatherEndpointViewModel!! as T
    }
}