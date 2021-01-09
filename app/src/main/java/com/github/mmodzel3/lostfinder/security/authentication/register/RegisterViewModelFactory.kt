package com.github.mmodzel3.lostfinder.security.authentication.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager

class RegisterViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val registerRepository: RegisterRepository = RegisterRepository.getInstance()
        return RegisterViewModel(registerRepository) as T
    }
}