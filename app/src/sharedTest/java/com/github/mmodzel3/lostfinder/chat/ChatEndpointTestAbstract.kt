package com.github.mmodzel3.lostfinder.chat

import com.github.mmodzel3.lostfinder.server.ServerEndpointTestAbstract
import com.github.mmodzel3.lostfinder.server.ServerResponse
import com.github.mmodzel3.lostfinder.user.User
import com.github.mmodzel3.lostfinder.user.UserEndpointTestAbstract
import com.github.mmodzel3.lostfinder.user.UserRole
import org.junit.Before
import java.util.*
import kotlin.collections.ArrayList

abstract class ChatEndpointTestAbstract : ServerEndpointTestAbstract() {
    companion object {
        const val MSG_USER_ID = "123456"
        const val MSG_USER_NAME = "example"
        const val MSG_USER_EMAIL = "example@example.com"
        val MSG_USER_ROLE = UserRole.OWNER
        const val MSG_TEXT = "example text"
        const val DAY_BEFORE_IN_MILLISECONDS = 24*60*60*1000
    }

    protected lateinit var chatEndpoint: ChatEndpoint
    protected lateinit var messages: MutableList<ChatMessage>
    protected lateinit var user: User

    @Before
    override fun setUp() {
        super.setUp()
        createTestUser()
        createTestMessages()

        chatEndpoint = ChatEndpointFactory.createChatEndpoint(null)
    }

    fun mockGetMessagesResponse() {
        mockServerJsonResponse(messages)
    }

    fun mockSendMessageResponse() {
        mockServerJsonResponse(messages[0])
    }

    protected fun createTestMessages() {
        messages = ArrayList()

        val yesterday = Date(System.currentTimeMillis() - UserEndpointTestAbstract.DAY_BEFORE_IN_MILLISECONDS)

        for (id in 1..4) {
            messages.add(ChatMessage(id.toString(), user,
                MSG_TEXT, yesterday, yesterday, yesterday))
        }
    }

    protected fun createTestUser() {
        user = User(MSG_USER_ID, MSG_USER_EMAIL, null,
            MSG_USER_NAME, MSG_USER_ROLE, null, Date(), Date(), false, false, null)
    }
}