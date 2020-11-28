package com.github.mmodzel3.lostfinder.chat

import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManagerStub
import com.google.common.truth.Truth.assertThat
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import java.lang.Exception
import java.util.concurrent.TimeUnit

class ChatActivityTest : ChatEndpointTestAbstract() {
    companion object {
        const val TEST_MESSAGE = "message"
        const val TEST_EMPTY_MESSAGE = "      "
    }

    private lateinit var chatScenario: ActivityScenario<ChatActivity>

    @Before
    override fun setUp() {
        super.setUp()

        TokenManager.tokenManager = TokenManagerStub.getInstance()
        mockGetMessagesResponse()

        chatScenario = ActivityScenario.launch(ChatActivity::class.java)
        server.takeRequest()
    }

    @After
    override fun tearDown() {
        super.tearDown()

        chatScenario.close()
    }

    @Test
    fun whenOpenActivityThenMessagesFromServerAreShown() {
        chatScenario.onActivity {
            val recyclerView: RecyclerView = it.findViewById(R.id.activity_chat_rv_message_list)

            assertThat(recyclerView.size).isEqualTo(messages.size)
        }
    }

    @Test
    fun whenSendMessageWithTextThenItIsSend() {
        mockSendMessageResponse()

        onView(withId(R.id.activity_chat_et_message))
            .perform(replaceText(TEST_MESSAGE))

        onView(withId(R.id.activity_chat_bt_send))
            .perform(click())

        val request: RecordedRequest? = server.takeRequest(1000, TimeUnit.MILLISECONDS)
        assertThat(request).isNotNull()
        assertThat(request?.bodySize).isGreaterThan(0)

        val message: ChatUserMessage = gson.fromJson(request?.body?.readUtf8(), ChatUserMessage::class.java)
        assertThat(message.msg).isEqualTo(TEST_MESSAGE)
    }

    @Test
    fun whenSendMessageWithOnlySpacesThenItIsNotSend() {
        mockSendMessageResponse()

        onView(withId(R.id.activity_chat_et_message))
            .perform(replaceText(TEST_EMPTY_MESSAGE))

        onView(withId(R.id.activity_chat_bt_send))
            .perform(click())

        val request: RecordedRequest? = server.takeRequest(1, TimeUnit.MILLISECONDS)
        assertThat(request).isNull()
    }
}