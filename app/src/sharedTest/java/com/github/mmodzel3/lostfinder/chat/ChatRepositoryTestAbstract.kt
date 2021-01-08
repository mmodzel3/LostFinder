package com.github.mmodzel3.lostfinder.chat

import org.junit.After
import org.junit.Before

abstract class ChatRepositoryTestAbstract : ChatEndpointTestAbstract() {
    protected lateinit var chatRepository: ChatRepository

    @Before
    override fun setUp() {
        super.setUp()

        chatRepository = ChatRepository.getInstance(null)
    }

    @After
    override fun tearDown() {
        super.tearDown()

        ChatRepository.clear()
    }
}