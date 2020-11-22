package com.github.mmodzel3.lostfinder.chat

import com.github.mmodzel3.lostfinder.server.ServerEndpointTestAbstract
import com.github.mmodzel3.lostfinder.server.ServerResponse
import com.github.mmodzel3.lostfinder.user.UserEndpointTestAbstract
import java.util.*
import kotlin.collections.ArrayList

abstract class ChatEndpointTestAbstract : ServerEndpointTestAbstract() {
    companion object {
        const val MSG_USER_ID = "123456"
        const val MSG_USER_NAME = "example"
        const val MSG_TEXT = "example text"
        const val DAY_BEFORE_IN_MILLISECONDS = 24*60*60*1000
    }

    protected lateinit var chatEndpoint: ChatEndpoint
    protected lateinit var messages: MutableList<ChatMessage>

    override fun setUp() {
        super.setUp()
        createTestMessages()

        chatEndpoint = ChatEndpointFactory.createChatEndpoint(null)
    }

    fun mockGetAllMessagesResponse() {
        mockServerJsonResponse(messages)
    }

    fun mockSendMessageResponse() {
        mockServerJsonResponse(ServerResponse.OK)
    }

    protected fun createTestMessages() {
        messages = ArrayList()

        val yesterday = Date(System.currentTimeMillis() - UserEndpointTestAbstract.DAY_BEFORE_IN_MILLISECONDS)

        for (id in 1..4) {
            messages.add(ChatMessage(id.toString(), MSG_USER_ID, MSG_USER_NAME,
                MSG_TEXT, yesterday, yesterday, yesterday))
        }
    }
}