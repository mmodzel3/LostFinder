package com.github.mmodzel3.lostfinder.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class WeatherEndpointViewModelFactory(private val weatherEndpoint: WeatherEndpoint,
                                      private val weatherApiKey: String,
                                      private val weatherUnits: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return WeatherEndpointViewModel(weatherEndpoint, weatherApiKey, weatherUnits) as T
    }
}