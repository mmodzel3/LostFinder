package com.github.mmodzel3.lostfinder.security.authentication.register

import com.github.mmodzel3.lostfinder.server.ServerEndpointFactory.createServerEndpoint

object RegisterEndpointFactory {
    fun createRegisterEndpoint(): RegisterEndpoint {
        return createServerEndpoint(RegisterEndpointErrorInterceptor())
    }
}