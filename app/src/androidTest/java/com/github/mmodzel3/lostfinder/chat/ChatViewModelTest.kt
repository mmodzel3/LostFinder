package com.github.mmodzel3.lostfinder.chat

import androidx.lifecycle.Observer
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatViewModelTest: ChatRepositoryTestAbstract() {
    companion object {
        const val MINUTE_IN_MILLISECONDS = 60 * 1000
    }

    private lateinit var chatViewModel: ChatViewModel
    private lateinit var latch: CountDownLatch

    @Before
    override fun setUp() {
        super.setUp()

        chatViewModel = ChatViewModel(chatRepository)
        latch = CountDownLatch(1)

        runBlocking(Dispatchers.Main) {
            chatViewModel.data.value = HashMap()
        }
    }

    @After
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun whenUpdateDataAndUserApiAccessErrorThenStatusIsError() {
        mockServerFailureResponse()

        observeAndWaitForStatusChange {
            runBlocking {
                chatViewModel.updateTask { chatViewModel.fetchAdditionalMessages() }
            }
        }

        assertThat(chatViewModel.status.value).isEqualTo(ServerEndpointStatus.ERROR)
    }

    @Test
    fun whenFetchAdditionalMessagesThenGotDataAdded() {
        mockGetMessagesResponse()

        observeAndWaitForStatusChange {
            runBlocking {
                chatViewModel.updateTask { chatViewModel.fetchAdditionalMessages() }
            }
        }

        assertThat(chatViewModel.data.value).hasSize(messages.size)

        val messagesIds = messages.map { it.id }
        chatViewModel.data.value!!.forEach {
            assertThat(messagesIds).contains(it.key)
            assertThat(messagesIds).contains(it.value.id)
        }
    }

    @Test
    fun whenFetchAdditionalMessagesAndHasDataCachedThenGotDataUpdated() {
        chatViewModel.data.value!!.putAll(changeTestMessagesToMap())
        updateTestMessages()

        mockGetMessagesResponse()

        observeAndWaitForStatusChange {
            runBlocking {
                chatViewModel.updateTask { chatViewModel.fetchAdditionalMessages() }
            }
        }

        assertThat(chatViewModel.data.value).hasSize(messages.size)

        val yesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS + MINUTE_IN_MILLISECONDS)
        val messagesIds = messages.map { it.id }
        chatViewModel.data.value!!.forEach {
            assertThat(messagesIds).contains(it.key)
            assertThat(messagesIds).contains(it.value.id)
            assertThat(it.value.lastUpdateDate).isAtLeast(yesterday)
        }
    }

    private fun observeAndWaitForStatusChange(doAfterObserving: () -> Unit) {
        val observer = Observer<ServerEndpointStatus> {
            latch.countDown()
        }

        runBlocking(Dispatchers.Main) {
            chatViewModel.status.observeForever(observer)
        }

        doAfterObserving()
        latch.await(2000, TimeUnit.MILLISECONDS)

        runBlocking(Dispatchers.Main) {
            chatViewModel.status.removeObserver(observer)
        }
    }

    private fun changeTestMessagesToMap() : MutableMap<String, ChatMessage> {
        val map : MutableMap<String, ChatMessage> = HashMap()
        messages.forEach { map[it.id] = it }

        return map
    }

    private fun updateTestMessages(): MutableList<ChatMessage> {
        val newMessages: MutableList<ChatMessage> = ArrayList()

        messages.forEach {
            newMessages.add(ChatMessage(it.id, it.user, it.msg, it.sendDate, it.receivedDate, Date()))
        }

        messages = newMessages
        return newMessages
    }
}