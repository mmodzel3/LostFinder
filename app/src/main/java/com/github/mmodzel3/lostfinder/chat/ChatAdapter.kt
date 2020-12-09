package com.github.mmodzel3.lostfinder.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager


class ChatAdapter(private val tokenManager: TokenManager) : RecyclerView.Adapter<ChatViewHolder>() {
    companion object {
        const val MSG_RECV_TYPE = 1
        const val MSG_SEND_TYPE = 2
    }

    var messages: MutableList<ChatMessage> = ArrayList()
        set(value: MutableList<ChatMessage>) {
            value.sortByDescending { it.sendDate }
            field = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return if (viewType == MSG_RECV_TYPE) {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_chat_recv_msg, parent, false)

            ChatRecvViewHolder(itemView)
        } else {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.activity_chat_send_msg, parent, false)

            ChatSendViewHolder(itemView)
        }
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message: ChatMessage = messages[position]

        if (getItemViewType(position) == MSG_RECV_TYPE) {
            val chatRecvViewHolder: ChatRecvViewHolder = holder as ChatRecvViewHolder
            chatRecvViewHolder.userName = message.user.username
        }

        holder.text = message.msg
        holder.time = message.sendDate
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].user.email != tokenManager.getTokenEmailAddress()) {
            MSG_RECV_TYPE
        } else {
            MSG_SEND_TYPE
        }
    }
}