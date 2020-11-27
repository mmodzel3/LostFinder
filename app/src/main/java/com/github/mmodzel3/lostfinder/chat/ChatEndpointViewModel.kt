package com.github.mmodzel3.lostfinder.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus
import com.github.mmodzel3.lostfinder.server.ServerPushEndpointViewModelAbstract
import kotlinx.coroutines.launch

class ChatEndpointViewModel (private val chatEndpoint: ChatEndpoint) : ServerPushEndpointViewModelAbstract<ChatMessage>() {
    companion object {
        const val MESSAGES_PER_PAGE = 10
        const val CHAT_MESSAGE_PUSH_NOTIFICATION_TYPE = "chat"
    }

    val messages: MutableLiveData<MutableMap<String, ChatMessage>>
        get() = data

    init {
        listenToChatMessagesNotifications()
        forceFetchAdditionalMessages()
    }

    fun forceFetchAdditionalMessages() {
        status.postValue(ServerEndpointStatus.FETCHING)

        viewModelScope.launch {
            fetchAdditionalMessages()
            status.postValue(ServerEndpointStatus.OK)
        }
    }

    override suspend fun fetchAllData() {
        try {
            val pages: Int = dataCache.size / MESSAGES_PER_PAGE;
            for (page: Int in 0..pages) {
                fetchMessages(page)
            }
        } catch (e: InvalidTokenException) {
            status.postValue(ServerEndpointStatus.INVALID_TOKEN)
        } catch (e: ChatEndpointAccessErrorException) {
            status.postValue(ServerEndpointStatus.ERROR)
        }
    }

    internal suspend fun fetchAdditionalMessages() {
        try {
            val page: Int = dataCache.size / MESSAGES_PER_PAGE;
            fetchMessages(page)
        } catch (e: InvalidTokenException) {
            status.postValue(ServerEndpointStatus.INVALID_TOKEN)
        } catch (e: ChatEndpointAccessErrorException) {
            status.postValue(ServerEndpointStatus.ERROR)
        }
    }

    private suspend fun fetchMessages(page: Int) {
        val messagesData: List<ChatMessage> = chatEndpoint.getMessages(page, MESSAGES_PER_PAGE)
        update(messagesData)
    }

    private fun listenToChatMessagesNotifications() {
        listenToPushNotifications<ChatMessage>(CHAT_MESSAGE_PUSH_NOTIFICATION_TYPE) {
            update(listOf(it))
        }
    }
}