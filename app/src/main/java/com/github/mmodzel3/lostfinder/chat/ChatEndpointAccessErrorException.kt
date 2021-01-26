package com.github.mmodzel3.lostfinder.chat

import com.github.mmodzel3.lostfinder.server.ServerEndpointAccessErrorException

class ChatEndpointAccessErrorException : ServerEndpointAccessErrorException("Chat API access error") {
}
