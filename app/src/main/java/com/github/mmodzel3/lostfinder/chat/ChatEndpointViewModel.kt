package com.github.mmodzel3.lostfinder.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mmodzel3.lostfinder.notification.PushNotificationChatMessageConverter
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus
import com.github.mmodzel3.lostfinder.server.ServerPushEndpointViewModelAbstract
import kotlinx.coroutines.launch

class ChatEndpointViewModel (private val chatEndpoint: ChatEndpoint) : ServerPushEndpointViewModelAbstract<ChatMessage>() {
    companion object {
        const val MESSAGES_PER_PAGE = 10
    }

    val messages: MutableLiveData<MutableMap<String, ChatMessage>>
        get() = data

    private val pushNotificationChatMessageConverter = PushNotificationChatMessageConverter.getInstance()

    init {
        listenToChatMessagesNotifications()
        forceFetchAdditionalMessages()
    }

    fun forceFetchAdditionalMessages() {
        runUpdate { fetchAdditionalMessages() }
    }

    internal suspend fun fetchAdditionalMessages(): List<ChatMessage> {
        val page: Int = dataCache.size / MESSAGES_PER_PAGE;
        return fetchMessages(page)
    }

    private suspend fun fetchMessages(page: Int): List<ChatMessage> {
        return chatEndpoint.getMessages(page, MESSAGES_PER_PAGE)
    }

    private fun listenToChatMessagesNotifications() {
        listenToPushNotifications(pushNotificationChatMessageConverter) {
            update(listOf(it))
        }
    }
}