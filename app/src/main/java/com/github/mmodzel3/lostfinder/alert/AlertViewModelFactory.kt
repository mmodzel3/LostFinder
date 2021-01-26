package com.github.mmodzel3.lostfinder.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager

class AlertViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val alertRepository: AlertRepository = AlertRepository.getInstance(tokenManager)
        return AlertViewModel(alertRepository) as T
    }
}