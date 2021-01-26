package com.github.mmodzel3.lostfinder.chat

import android.view.View

class ChatRecvViewHolder(itemView: View) : ChatViewHolder(itemView) {
    var userName: CharSequence
        get() {
            return userNameTextView.text
        }

        set(value: CharSequence) {
            userNameTextView.text = value
        }
}