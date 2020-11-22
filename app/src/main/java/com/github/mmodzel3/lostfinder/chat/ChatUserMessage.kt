package com.github.mmodzel3.lostfinder.chat

import java.util.*

data class ChatUserMessage(override val msg: String,
                           override val sendDate: Date) : ChatMessageBase {
}