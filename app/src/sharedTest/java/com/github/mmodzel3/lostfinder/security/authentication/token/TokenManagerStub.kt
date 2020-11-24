package com.github.mmodzel3.lostfinder.security.authentication.token

class TokenManagerStub(private val userEmail: String) : TokenManager(null) {
    companion object {
        const val TOKEN = "TOKEN"

        fun getInstance(userEmail: String): TokenManagerStub {
            return TokenManagerStub(userEmail)
        }
    }

    override fun getTokenEmailAddress() : String {
        return userEmail
    }

    override suspend fun getToken(): String {
        return TOKEN
    }
}