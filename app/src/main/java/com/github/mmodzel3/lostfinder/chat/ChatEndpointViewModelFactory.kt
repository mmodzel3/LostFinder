package com.github.mmodzel3.lostfinder.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatEndpointViewModelFactory(private val chatEndpoint: ChatEndpoint) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatEndpointViewModel(chatEndpoint) as T
    }
}