package com.github.mmodzel3.lostfinder.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager

class UserViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val userRepository: UserRepository = UserRepository.getInstance(tokenManager)
        return UserViewModel(userRepository) as T
    }
}