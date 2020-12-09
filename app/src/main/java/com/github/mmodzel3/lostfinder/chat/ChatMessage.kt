package com.github.mmodzel3.lostfinder.chat

import com.github.mmodzel3.lostfinder.server.ServerEndpointData
import com.github.mmodzel3.lostfinder.user.User
import java.util.*

data class ChatMessage(override val id: String,
                       val user: User,
                       override val msg: String,
                       override val sendDate: Date,
                       val receivedDate: Date,
                       override val lastUpdateDate: Date) : ChatMessageBase, ServerEndpointData {
}