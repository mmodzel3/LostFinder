package com.github.mmodzel3.lostfinder.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatEndpointViewModelFactory(private val chatEndpoint: ChatEndpoint) : ViewModelProvider.Factory {
    companion object {
        private var chatEndpointViewModel: ChatEndpointViewModel? = null
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (chatEndpointViewModel == null) {
            chatEndpointViewModel = ChatEndpointViewModel(chatEndpoint)
        }

        return chatEndpointViewModel!! as T
    }
}