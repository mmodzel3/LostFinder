package com.github.mmodzel3.lostfinder.security.authentication.logout

import com.github.mmodzel3.lostfinder.security.authentication.login.LoginRepository
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerRepositoryAbstract
import com.github.mmodzel3.lostfinder.server.ServerResponse

class LogoutRepository private constructor(tokenManager: TokenManager?) : ServerRepositoryAbstract() {
    companion object {
        private var logoutRepository: LogoutRepository? = null

        fun getInstance(tokenManager: TokenManager?): LogoutRepository {
            if (logoutRepository == null) {
                logoutRepository = LogoutRepository(tokenManager)
            }

            return logoutRepository!!
        }

        fun clear() {
            logoutRepository = null
        }
    }

    private val logoutEndpoint: LogoutEndpoint by lazy {
        LogoutEndpointFactory.createLogoutEndpoint(tokenManager)
    }

    suspend fun logout(): ServerResponse {
        return logoutEndpoint.logout()
    }
}