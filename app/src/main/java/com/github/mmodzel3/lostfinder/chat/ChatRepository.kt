package com.github.mmodzel3.lostfinder.chat

import androidx.lifecycle.MutableLiveData
import com.github.mmodzel3.lostfinder.notification.PushNotificationChatMessageConverter
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerPushRepositoryAbstract
import com.github.mmodzel3.lostfinder.server.ServerResponse

class ChatRepository private constructor (private val tokenManager: TokenManager?) : ServerPushRepositoryAbstract<ChatMessage>() {
    companion object {
        const val MESSAGES_PER_PAGE = 10

        var chatRepository: ChatRepository? = null

        fun getInstance(tokenManager: TokenManager?): ChatRepository {
            if (chatRepository == null) {
                chatRepository = ChatRepository(tokenManager)
            }

            return chatRepository!!
        }

        fun clear() {
            chatRepository = null
        }
    }

    val messages: MutableLiveData<MutableMap<String, ChatMessage>>
        get() = data

    private val pushNotificationChatMessageConverter = PushNotificationChatMessageConverter.getInstance()

    private val chatEndpoint: ChatEndpoint by lazy {
        ChatEndpointFactory.createChatEndpoint(tokenManager)
    }

    init {
        listenToChatMessagesNotifications()
    }

    suspend fun addMessage(chatUserMessage: ChatUserMessage): ServerResponse {
        return chatEndpoint.sendMessage(chatUserMessage)
    }

    suspend fun fetchAllMessages(): List<ChatMessage> {
        val messages: MutableList<ChatMessage> = ArrayList()
        val pages: Int = dataCache.size / MESSAGES_PER_PAGE

        for (page in 0..pages) {
            messages.addAll(fetchMessages(page))
        }

        return messages
    }

    suspend fun fetchAdditionalMessages(): List<ChatMessage> {
        val page: Int = dataCache.size / MESSAGES_PER_PAGE
        return fetchMessages(page)
    }

    private suspend fun fetchMessages(page: Int): List<ChatMessage> {
        return fetchAndUpdate { chatEndpoint.getMessages(page, MESSAGES_PER_PAGE) }
    }

    private fun listenToChatMessagesNotifications() {
        listenToPushNotifications(pushNotificationChatMessageConverter) {
            update(listOf(it))
        }
    }
}