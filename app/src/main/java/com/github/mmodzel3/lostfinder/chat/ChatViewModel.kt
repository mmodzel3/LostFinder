package com.github.mmodzel3.lostfinder.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mmodzel3.lostfinder.server.ServerResponse
import com.github.mmodzel3.lostfinder.server.ServerViewModelAbstract

class ChatViewModel (private val chatRepository: ChatRepository)
    : ServerViewModelAbstract<ChatMessage>(chatRepository) {

    val messages: MutableLiveData<MutableMap<String, ChatMessage>>
        get() = data

    override fun runUpdates() {
        runSingleUpdate { chatRepository.fetchAllMessages() }
    }

    fun addMessage(chatUserMessage: ChatUserMessage): LiveData<ServerResponse> {
        return convertServerRequestToLiveData { chatRepository.addMessage(chatUserMessage) }
    }

    fun forceFetchAdditionalMessages() {
        runSingleUpdate { fetchAdditionalMessages() }
    }

    internal suspend fun fetchAdditionalMessages(): List<ChatMessage> {
        return chatRepository.fetchAdditionalMessages()
    }
}