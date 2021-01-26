package com.github.mmodzel3.lostfinder.chat

import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerEndpointFactory.createServerEndpoint

object ChatEndpointFactory {
    fun createChatEndpoint(tokenManager: TokenManager?): ChatEndpoint {
        return createServerEndpoint(ChatEndpointErrorInterceptor(), tokenManager)
    }
}