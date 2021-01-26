package com.github.mmodzel3.lostfinder.chat

import android.view.View
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManagerStub
import com.google.common.truth.Truth.assertThat
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matchers.isEmptyString
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class ChatActivityTest : ChatRepositoryTestAbstract() {
    companion object {
        const val TEST_MESSAGE = "message"
        const val TEST_EMPTY_MESSAGE = "      "
    }

    private lateinit var chatScenario: ActivityScenario<ChatActivity>
    private lateinit var decorView: View

    @Before
    override fun setUp() {
        super.setUp()

        TokenManager.tokenManager = TokenManagerStub.getInstance()
    }

    @After
    override fun tearDown() {
        super.tearDown()

        chatScenario.close()
    }

    @Test
    fun whenOpenActivityThenMessagesFromServerAreShown() {
        startActivityNormally()

        chatScenario.onActivity {
            val recyclerView: RecyclerView = it.findViewById(R.id.activity_chat_rv_message_list)

            assertThat(recyclerView.size).isEqualTo(messages.size)
        }
    }

    @Test
    fun whenOpenActivityAndApiAccessErrorThenErrorToastIsShown() {
        startActivityWithApiAccessError()

        Thread.sleep(1000)
        onView(withText(R.string.activity_chat_err_fetching_msg_api_access_problem))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenOpenActivityWithInvalidCredentialsThenErrorToastIsShown() {
        startActivityWithInvalidCredentials()

        Thread.sleep(1000)
        onView(withText(R.string.activity_chat_err_fetching_msg_invalid_token))
                .inRoot(withDecorView(not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenSendMessageWithTextThenItIsSend() {
        startActivityNormally()
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
    fun whenSendMessageWithTextThenMessageEditTextIsCleared() {
        startActivityNormally()
        mockSendMessageResponse()

        onView(withId(R.id.activity_chat_et_message))
            .perform(replaceText(TEST_MESSAGE))

        onView(withId(R.id.activity_chat_bt_send))
            .perform(click())

        server.takeRequest(1000, TimeUnit.MILLISECONDS)

        onView(withId(R.id.activity_chat_et_message))
            .check(matches(withText(isEmptyString())))
    }

    @Test
    fun whenSendMessageWithOnlySpacesThenItIsNotSend() {
        startActivityNormally()
        mockSendMessageResponse()

        onView(withId(R.id.activity_chat_et_message))
            .perform(replaceText(TEST_EMPTY_MESSAGE))

        onView(withId(R.id.activity_chat_bt_send))
            .perform(click())

        val request: RecordedRequest? = server.takeRequest(500, TimeUnit.MILLISECONDS)
        assertThat(request).isNull()
    }

    @Test
    fun whenSendMessageAndHasProblemWithChatApiAccessThenErrorToastIsShown() {
        startActivityNormally()
        mockServerFailureResponse()

        onView(withId(R.id.activity_chat_et_message))
            .perform(replaceText(TEST_MESSAGE))

        onView(withId(R.id.activity_chat_bt_send))
            .perform(click())

        onView(withText(R.string.activity_chat_err_sending_msg_api_access_problem))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenSendMessageAndHasInvalidCredentialsThenErrorToastIsShown() {
        startActivityNormally()
        mockInvalidCredentialsResponse()

        onView(withId(R.id.activity_chat_et_message))
            .perform(replaceText(TEST_MESSAGE))

        onView(withId(R.id.activity_chat_bt_send))
            .perform(click())

        Thread.sleep(1000)
        onView(withText(R.string.activity_chat_err_sending_msg_invalid_token))
            .inRoot(withDecorView(not(decorView)))
            .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    private fun startActivityNormally() {
        mockGetMessagesResponse()

        chatScenario = ActivityScenario.launch(ChatActivity::class.java)
        server.takeRequest()

        chatScenario.onActivity {
            decorView = it.window.decorView
        }

        Thread.sleep(1000)
    }

    private fun startActivityWithApiAccessError() {
        mockServerFailureResponse()

        chatScenario = ActivityScenario.launch(ChatActivity::class.java)
        server.takeRequest()

        chatScenario.onActivity {
            decorView = it.window.decorView
        }

        Thread.sleep(1000)
    }

    private fun startActivityWithInvalidCredentials() {
        mockInvalidCredentialsResponse()

        chatScenario = ActivityScenario.launch(ChatActivity::class.java)
        server.takeRequest()

        chatScenario.onActivity {
            decorView = it.window.decorView
        }

        Thread.sleep(1000)
    }
}