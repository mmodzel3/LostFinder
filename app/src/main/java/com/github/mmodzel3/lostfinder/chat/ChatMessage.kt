package com.github.mmodzel3.lostfinder.chat

import com.github.mmodzel3.lostfinder.server.ServerEndpointData
import java.util.*

data class ChatMessage(override val id: String,
                       val userId: String,
                       val username: String,
                       override val msg: String,
                       override val sendDate: Date,
                       val receivedDate: Date,
                       override val lastUpdateDate: Date) : ChatMessageBase, ServerEndpointData {
}