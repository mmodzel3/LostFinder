package com.github.mmodzel3.lostfinder.chat

import com.github.mmodzel3.lostfinder.server.ServerEndpointAccessErrorException

class ChatEndpointAccessErrorException : ServerEndpointAccessErrorException("User API access error") {
}
