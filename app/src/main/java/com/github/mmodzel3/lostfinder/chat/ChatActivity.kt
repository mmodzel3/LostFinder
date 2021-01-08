package com.github.mmodzel3.lostfinder.chat

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.LoggedUserActivityAbstract
import com.github.mmodzel3.lostfinder.notification.PushNotificationChatMessageConverter
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus
import kotlinx.coroutines.launch
import java.util.*

open class ChatActivity : LoggedUserActivityAbstract() {
    private val tokenManager: TokenManager by lazy {
        TokenManager.getInstance(applicationContext)
    }

    private val chatViewModel: ChatViewModel by viewModels {
        ChatViewModelFactory(tokenManager)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: Button
    private lateinit var messageEditText: EditText

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatViewModelDataObserver: Observer<in MutableMap<String, ChatMessage>>
    private lateinit var chatViewModelStatusObserver: Observer<ServerEndpointStatus>

    private var firstFetchMessages = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)

        recyclerView = findViewById(R.id.activity_chat_rv_message_list)
        sendButton = findViewById(R.id.activity_chat_bt_send)
        messageEditText = findViewById(R.id.activity_chat_et_message)

        chatAdapter = ChatAdapter(tokenManager)

        initRecyclerView()
        observechatViewModel()
        initSendButton()
    }

    override fun onResume() {
        super.onResume()

        chatViewModel.runUpdates()
        PushNotificationChatMessageConverter.getInstance().showNotifications = false
    }

    override fun onPause() {
        super.onPause()

        PushNotificationChatMessageConverter.getInstance().showNotifications = true
        chatViewModel.stopUpdates()
    }

    override fun onDestroy() {
        super.onDestroy()

        chatViewModel.messages.removeObserver(chatViewModelDataObserver)
        chatViewModel.status.removeObserver(chatViewModelStatusObserver)

        PushNotificationChatMessageConverter.getInstance().showNotifications = true
    }

    private fun initRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.stackFromEnd = false

        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = chatAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(-1)) {
                    onChatScrollToEnd()
                }
            }
        })
    }

    private fun observechatViewModel() {
        observechatViewModelData()
        observechatViewModelStatus()
    }

    private fun observechatViewModelData() {
        chatViewModelDataObserver = Observer {
            chatAdapter.messages = it.values.toMutableList()
            chatAdapter.notifyDataSetChanged()

            if (firstFetchMessages) {
                recyclerView.scrollToPosition(0)
                firstFetchMessages = false
            }
        }

        chatViewModel.messages.observe(this, chatViewModelDataObserver)
    }

    private fun observechatViewModelStatus() {
        val activity: Activity = this

        chatViewModelStatusObserver = Observer {
            if (it == ServerEndpointStatus.ERROR) {
                Toast.makeText(activity, R.string.activity_chat_err_fetching_msg_api_access_problem,
                    Toast.LENGTH_LONG).show()
            } else if (it == ServerEndpointStatus.INVALID_TOKEN) {
                Toast.makeText(activity, R.string.activity_chat_err_fetching_msg_invalid_token,
                    Toast.LENGTH_LONG).show()
                goToLoginActivity()
            }
        }

        chatViewModel.status.observe(this, chatViewModelStatusObserver)
    }

    private fun initSendButton() {
        sendButton.setOnClickListener {
            onSendButtonClick()
        }

        enableSendButton()
    }

    private fun onSendButtonClick() {
        val text: String = messageEditText.text.toString()
        val message = ChatUserMessage(text, Date())

        if (text.trim() != "") {
            disableSendButton()
            sendMessage(message)
        }
    }

    private fun sendMessage(message: ChatUserMessage) {
        val activity: Activity = this

        lifecycleScope.launch {
            try {
                chatViewModel.addMessage(message)
                messageEditText.setText("")
                recyclerView.smoothScrollToPosition(0)
            } catch (e: ChatEndpointAccessErrorException) {
                Toast.makeText(activity, R.string.activity_chat_err_sending_msg_api_access_problem,
                    Toast.LENGTH_SHORT).show()
            } catch (e: InvalidTokenException) {
                Toast.makeText(activity, R.string.activity_chat_err_sending_msg_invalid_token,
                    Toast.LENGTH_LONG).show()

                goToLoginActivity()
            }

            enableSendButton()
        }
    }

    private fun onChatScrollToEnd() {
        if (!chatViewModel.status.equals(ServerEndpointStatus.FETCHING)) {
            chatViewModel.forceFetchAdditionalMessages()
        }
    }

    private fun enableSendButton() {
        sendButton.isEnabled = true
    }

    private fun disableSendButton() {
        sendButton.isEnabled = false
    }
}