package com.github.mmodzel3.lostfinder.chat

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus
import kotlinx.coroutines.launch
import java.util.*


open class ChatActivity : AppCompatActivity() {
    private val chatEndpoint: ChatEndpoint by lazy {
        ChatEndpointFactory.createChatEndpoint(TokenManager.getInstance(applicationContext))
    }

    private val chatEndpointViewModel: ChatEndpointViewModel by viewModels {
        ChatEndpointViewModelFactory(chatEndpoint)
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var sendButton: Button
    private lateinit var messageEditText: EditText

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var tokenManager: TokenManager
    private lateinit var chatEndpointViewModelObserver: Observer<in MutableMap<String, ChatMessage>>

    private var firstFetchMessages = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat)

        tokenManager = TokenManager.getInstance(applicationContext)

        recyclerView = findViewById(R.id.activity_chat_rv_message_list)
        sendButton = findViewById(R.id.activity_chat_bt_send)
        messageEditText = findViewById(R.id.activity_chat_et_message)

        chatAdapter = ChatAdapter(tokenManager)

        initRecyclerView()
        observeChatEndpointViewModel()
        initSendButton()
    }

    override fun onDestroy() {
        super.onDestroy()

        chatEndpointViewModel.messages.removeObserver(chatEndpointViewModelObserver)
    }

    private fun initRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!chatEndpointViewModel.status.equals(ServerEndpointStatus.FETCHING) &&
                    !recyclerView.canScrollVertically(-1)) {
                    chatEndpointViewModel.forceFetchAdditionalMessages()
                }
            }
        })
    }

    private fun observeChatEndpointViewModel() {
        chatEndpointViewModelObserver = Observer {
            chatAdapter.messages = it.values.toMutableList()
            chatAdapter.notifyDataSetChanged()

            if (firstFetchMessages) {
                recyclerView.scrollToPosition(it.values.size-1)
                firstFetchMessages = false
            }
        }

        chatEndpointViewModel.messages.observe(this, chatEndpointViewModelObserver)
    }

    private fun initSendButton() {
        sendButton.setOnClickListener {
            onSendButtonClick()
        }

        enableSendButton()
    }

    private fun onSendButtonClick() {
        val text: String = messageEditText.text.toString()
        val message: ChatUserMessage = ChatUserMessage(text, Date())

        disableSendButton()

        lifecycleScope.launch {
            chatEndpoint.sendMessage(message)
            messageEditText.text.clear()
            enableSendButton()
        }
    }

    private fun enableSendButton() {
        sendButton.isEnabled = true
    }

    private fun disableSendButton() {
        sendButton.isEnabled = false
    }
}