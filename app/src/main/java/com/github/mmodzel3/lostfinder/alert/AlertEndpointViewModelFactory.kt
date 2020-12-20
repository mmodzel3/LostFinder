package com.github.mmodzel3.lostfinder.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AlertEndpointViewModelFactory(private val alertEndpoint: AlertEndpoint) : ViewModelProvider.Factory {
    companion object {
        private var alertEndpointViewModel: AlertEndpointViewModel? = null
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (alertEndpointViewModel == null) {
            alertEndpointViewModel = AlertEndpointViewModel(alertEndpoint)
        }

        return alertEndpointViewModel!! as T
    }
}