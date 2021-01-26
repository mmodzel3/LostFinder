package com.github.mmodzel3.lostfinder.chat

import android.view.View
import com.github.mmodzel3.lostfinder.R

class ChatSendViewHolder(itemView: View) : ChatViewHolder(itemView) {
    init {
        userNameTextView.text = itemView.context.getString(R.string.activity_chat_msg_me)
    }

    val userName: CharSequence
        get() {
            return userNameTextView.text
        }
}