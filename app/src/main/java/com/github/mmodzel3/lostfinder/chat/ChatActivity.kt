package com.github.mmodzel3.lostfinder.chat

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mmodzel3.lostfinder.MainActivity
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.alert.AlertActivity
import com.github.mmodzel3.lostfinder.notification.PushNotificationChatMessageConverter
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginActivity
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus
import com.github.mmodzel3.lostfinder.weather.WeatherActivity
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
    private lateinit var chatEndpointViewModelDataObserver: Observer<in MutableMap<String, ChatMessage>>
    private lateinit var chatEndpointViewModelStatusObserver: Observer<ServerEndpointStatus>

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

    override fun onResume() {
        super.onResume()

        PushNotificationChatMessageConverter.getInstance().showNotifications = false
    }

    override fun onPause() {
        super.onPause()

        PushNotificationChatMessageConverter.getInstance().showNotifications = true
    }

    override fun onDestroy() {
        super.onDestroy()

        chatEndpointViewModel.messages.removeObserver(chatEndpointViewModelDataObserver)
        chatEndpointViewModel.status.removeObserver(chatEndpointViewModelStatusObserver)

        PushNotificationChatMessageConverter.getInstance().showNotifications = true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_chat, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id: Int = item.itemId
        return if (id == R.id.activity_chat_it_map) {
            goToMapActivity()
            true
        } else if (id == R.id.activity_chat_it_alert) {
            goToAlertActivity()
            true
        } else if (id == R.id.activity_chat_it_weather) {
            goToWeatherActivity()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
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

    private fun observeChatEndpointViewModel() {
        observeChatEndpointViewModelData()
        observeChatEndpointViewModelStatus()
    }

    private fun observeChatEndpointViewModelData() {
        chatEndpointViewModelDataObserver = Observer {
            chatAdapter.messages = it.values.toMutableList()
            chatAdapter.notifyDataSetChanged()

            if (firstFetchMessages) {
                recyclerView.scrollToPosition(0)
                firstFetchMessages = false
            }
        }

        chatEndpointViewModel.messages.observe(this, chatEndpointViewModelDataObserver)
    }

    private fun observeChatEndpointViewModelStatus() {
        val activity: Activity = this

        chatEndpointViewModelStatusObserver = Observer {
            if (it == ServerEndpointStatus.ERROR) {
                Toast.makeText(activity, R.string.activity_chat_err_fetching_msg_api_access_problem,
                    Toast.LENGTH_LONG).show()
            } else if (it == ServerEndpointStatus.INVALID_TOKEN) {
                Toast.makeText(activity, R.string.activity_chat_err_fetching_msg_invalid_token,
                    Toast.LENGTH_LONG).show()
                goToLoginActivity()
            }
        }

        chatEndpointViewModel.status.observe(this, chatEndpointViewModelStatusObserver)
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

        if (text.trim() != "") {
            disableSendButton()
            sendMessage(message)
        }
    }

    private fun sendMessage(message: ChatUserMessage) {
        val activity: Activity = this

        lifecycleScope.launch {
            try {
                chatEndpoint.sendMessage(message)
                messageEditText.setText("")
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
        if (!chatEndpointViewModel.status.equals(ServerEndpointStatus.FETCHING)) {
            chatEndpointViewModel.forceFetchAdditionalMessages()
        }
    }

    private fun enableSendButton() {
        sendButton.isEnabled = true
    }

    private fun disableSendButton() {
        sendButton.isEnabled = false
    }

    private fun goToMapActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }

    private fun goToAlertActivity() {
        val intent = Intent(this, AlertActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }

    private fun goToWeatherActivity() {
        val intent = Intent(this, WeatherActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
        finish()
    }
}