package com.github.mmodzel3.lostfinder.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager

class ChatViewModelFactory(private val tokenManager: TokenManager) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val chatRepository: ChatRepository = ChatRepository.getInstance(tokenManager)
        return ChatViewModel(chatRepository) as T
    }
}