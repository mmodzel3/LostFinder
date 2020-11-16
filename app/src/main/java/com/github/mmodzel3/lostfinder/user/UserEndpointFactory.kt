package com.github.mmodzel3.lostfinder.user

import com.github.mmodzel3.lostfinder.security.authentication.token.TokenAuthServiceBinder
import com.github.mmodzel3.lostfinder.server.ServerEndpointFactory.createServerEndpoint

object UserEndpointFactory {
    fun createUserEndpoint(tokenAuthServiceBinder: TokenAuthServiceBinder): UserEndpoint {
        return createServerEndpoint(tokenAuthServiceBinder, UserEndpointErrorInterceptor())
    }
}