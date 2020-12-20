package com.github.mmodzel3.lostfinder.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserEndpointViewModelFactory(private val userEndpoint: UserEndpoint) : ViewModelProvider.Factory {
    companion object {
        private var userEndpointViewModel: UserEndpointViewModel? = null
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (userEndpointViewModel == null) {
            userEndpointViewModel = UserEndpointViewModel(userEndpoint)
        }

        return userEndpointViewModel!! as T
    }
}