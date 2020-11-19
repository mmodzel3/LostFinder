package com.github.mmodzel3.lostfinder.authentication.token

import androidx.test.core.app.ApplicationProvider
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager

class TokenManagerStub : TokenManager(null) {
    companion object {
        const val TOKEN = "token1.token2.token3"
        const val LOGGED_USER_EMAIL = "example@example.com"

        fun getInstance() : TokenManager {
            return TokenManagerStub()
        }
    }

    override suspend fun getToken() : String {
        return TOKEN
    }

    override fun getTokenEmailAddress() : String {
        return LOGGED_USER_EMAIL
    }
}