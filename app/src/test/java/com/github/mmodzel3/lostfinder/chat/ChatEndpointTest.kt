package com.github.mmodzel3.lostfinder.chat

import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class ChatEndpointTest : ChatEndpointTestAbstract() {
    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun whenErrorOnEndpointThenGotChatEndpointAccessErrorException() {
        mockServerFailureResponse()

        assertThrows<ChatEndpointAccessErrorException> {
            runBlocking {
                chatEndpoint.getMessages(0, 1000)
            }
        }
    }
}