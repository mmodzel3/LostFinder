package com.github.mmodzel3.lostfinder.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AlertEndpointViewModelFactory(private val alertEndpoint: AlertEndpoint) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AlertEndpointViewModel(alertEndpoint) as T
    }
}