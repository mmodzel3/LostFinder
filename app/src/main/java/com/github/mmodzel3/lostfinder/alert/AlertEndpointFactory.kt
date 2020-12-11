package com.github.mmodzel3.lostfinder.alert

import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerEndpointFactory.createServerEndpoint

object AlertEndpointFactory {
    fun createAlertEndpoint(tokenManager: TokenManager?): AlertEndpoint {
        return createServerEndpoint(AlertEndpointErrorInterceptor(), tokenManager)
    }
}