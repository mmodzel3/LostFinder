package com.github.mmodzel3.lostfinder.security.authentication.token

import com.github.mmodzel3.lostfinder.user.UserRole

class TokenManagerStub(private val userEmail: String,
                       private val userName: String,
                       private val userRole: UserRole) : TokenManager(null) {
    companion object {
        const val TOKEN = "TOKEN"
        const val USER_EMAIL = "example@example.com"
        val USER_ROLE = UserRole.OWNER
        const val USER_NAME = "username"

        fun getInstance(userEmail: String = USER_EMAIL, userName: String = USER_NAME,
                        userRole: UserRole = USER_ROLE): TokenManagerStub {
            return TokenManagerStub(userEmail, userName, userRole)
        }
    }

    override fun getTokenEmailAddress(): String {
        return userEmail
    }

    override fun getTokenUsername(): String {
        return userName
    }

    override fun getTokenRole(): UserRole {
        return userRole
    }

    override suspend fun getToken(): String {
        return TOKEN
    }

    override suspend fun logout(): Boolean {
        return true
    }
}