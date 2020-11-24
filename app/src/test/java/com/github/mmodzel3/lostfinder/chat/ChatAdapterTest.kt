package com.github.mmodzel3.lostfinder.chat

import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManagerStub
import com.github.mmodzel3.lostfinder.user.User
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapterTest {
    companion object {
        const val DAY_BEFORE_IN_MILLISECONDS = 24*60*60*1000
        const val MINUTE_IN_MILLISECONDS = 60*1000

        const val MSG_TEXT = "text"

        const val USER_ID1 = "1"
        const val USER_EMAIL1 = "example@example.com"
        const val USER_NAME1 = "user"
        const val USER_ROLE1 = "USER"
        const val USER1_MSG_COUNT = 4
        const val USER_ID2 = "2"
        const val USER_EMAIL2 = "example1@example.com"
        const val USER_NAME2 = "user1"
        const val USER_ROLE2 = "OWNER"
        const val USER2_MSG_COUNT = 4
    }

    private lateinit var parentView: View
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messages: MutableList<ChatMessage>
    private lateinit var user1: User
    private lateinit var user2: User

    @Before
    fun setUp() {
        mockView()

        chatAdapter = ChatAdapter(TokenManagerStub.getInstance(USER_EMAIL1))
        createTestUsers()
        createTestMessages()

        chatAdapter.messages = messages
    }

    @Test
    fun whenGetItemViewTypeForLoggedUserThenGotCorrectType() {
        val type: Int = chatAdapter.getItemViewType(USER1_MSG_COUNT-1)

        assertThat(type).isEqualTo(ChatAdapter.MSG_SEND_TYPE)
    }

    @Test
    fun whenGetItemViewTypeForOtherUserThanLoggedThenGotCorrectType() {
        val type: Int = chatAdapter.getItemViewType(USER1_MSG_COUNT+1)

        assertThat(type).isEqualTo(ChatAdapter.MSG_RECV_TYPE)
    }

    @Test
    fun whenOnBindViewHolderOnLoggedUserMessageThenGotUpdatedCorrectMessage() {
        val holder = ChatSendViewHolder(parentView)
        chatAdapter.onBindViewHolder(holder, USER1_MSG_COUNT-1)

        val yesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS - MINUTE_IN_MILLISECONDS)
        assertThat(holder.text).isEqualTo(MSG_TEXT)
        assertThat(holder.time).isAtLeast(yesterday)
    }

    @Test
    fun whenOnBindViewHolderOnOtherUserMessageThenGotUpdatedCorrectMessage() {
        val holder = ChatRecvViewHolder(parentView)
        chatAdapter.onBindViewHolder(holder, USER1_MSG_COUNT+1)

        val yesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS - MINUTE_IN_MILLISECONDS)
        assertThat(holder.userName).isEqualTo(USER_NAME2)
        assertThat(holder.text).isEqualTo(MSG_TEXT)
        assertThat(holder.time).isAtLeast(yesterday)
    }

    private fun mockView() {
        val context: Context = Mockito.mock(Context::class.java)
        parentView = Mockito.mock(View::class.java)

        `when`(parentView.context).thenReturn(context)
        `when`(context.getString(Mockito.anyInt())).thenReturn("Me")
        `when`(parentView.findViewById<TextView>(Mockito.anyInt())).thenAnswer {
            val textView: TextView = Mockito.mock(TextView::class.java)
            var text = ""

            `when`(textView.text).then {
                return@then text
            }

            `when`(textView.setText(Mockito.anyString())).then {
                text = it.arguments[0] as String
                return@then Unit
            }

            return@thenAnswer textView
        }
    }

    private fun createTestMessages() {
        messages = ArrayList()

        val yesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS)

        for (id in 1..USER1_MSG_COUNT) {
            messages.add(ChatMessage(id.toString(), user1,
                MSG_TEXT, yesterday, yesterday, yesterday))
        }

        for (id in 1..USER2_MSG_COUNT) {
            messages.add(ChatMessage(id.toString(), user2,
                MSG_TEXT, yesterday, yesterday, yesterday))
        }
    }

    private fun createTestUsers() {
        user1 = User(USER_ID1, USER_EMAIL1, null, USER_NAME1, USER_ROLE1, null, Date())
        user2 = User(USER_ID2, USER_EMAIL2, null, USER_NAME2, USER_ROLE2, null, Date())
    }
}