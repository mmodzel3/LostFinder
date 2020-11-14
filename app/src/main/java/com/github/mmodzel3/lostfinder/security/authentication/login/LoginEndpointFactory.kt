package com.github.mmodzel3.lostfinder.security.authentication.login

import com.github.mmodzel3.lostfinder.server.ServerEndpointFactory.createServerEndpoint

object LoginEndpointFactory {
    fun createLoginEndpoint(): LoginEndpoint {
        return createServerEndpoint(LoginEndpointErrorInterceptor())
    }
}