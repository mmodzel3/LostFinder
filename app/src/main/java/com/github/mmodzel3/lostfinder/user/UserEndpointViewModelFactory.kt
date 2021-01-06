package com.github.mmodzel3.lostfinder.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UserEndpointViewModelFactory(private val userEndpoint: UserEndpoint) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserEndpointViewModel(userEndpoint) as T
    }
}