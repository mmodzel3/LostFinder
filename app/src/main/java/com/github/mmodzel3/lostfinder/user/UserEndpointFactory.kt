package com.github.mmodzel3.lostfinder.user

import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerEndpointFactory.createServerEndpoint

object UserEndpointFactory {
    fun createUserEndpoint(tokenManager: TokenManager?): UserEndpoint {
        return createServerEndpoint(UserEndpointErrorInterceptor(), tokenManager)
    }
}