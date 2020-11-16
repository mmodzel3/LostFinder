package com.github.mmodzel3.lostfinder.user

import com.github.mmodzel3.lostfinder.server.ServerEndpointFactory.createServerEndpoint

object UserEndpointFactory {
    fun createLoginEndpoint(): UserEndpoint {
        return createServerEndpoint(UserEndpointErrorInterceptor())
    }
}