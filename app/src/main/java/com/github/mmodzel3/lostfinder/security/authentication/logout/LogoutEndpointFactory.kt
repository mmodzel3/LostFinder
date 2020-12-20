package com.github.mmodzel3.lostfinder.security.authentication.logout

import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerEndpointFactory.createServerEndpoint

object LogoutEndpointFactory {
    fun createLogoutEndpoint(tokenManager: TokenManager?): LogoutEndpoint {
        return createServerEndpoint(LogoutEndpointErrorInterceptor(), tokenManager)
    }
}