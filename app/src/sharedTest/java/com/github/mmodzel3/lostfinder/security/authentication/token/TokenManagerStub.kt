package com.github.mmodzel3.lostfinder.security.authentication.token

import com.github.mmodzel3.lostfinder.user.UserRole

class TokenManagerStub(private val userEmail: String,
                       private val userRole: UserRole) : TokenManager(null) {
    companion object {
        const val TOKEN = "TOKEN"
        const val USER_EMAIL = "example@example.com"
        val USER_ROLE = UserRole.OWNER

        fun getInstance(userEmail: String = USER_EMAIL, userRole: UserRole = USER_ROLE): TokenManagerStub {
            return TokenManagerStub(userEmail, userRole)
        }
    }

    override fun getTokenEmailAddress() : String {
        return userEmail
    }

    override fun getTokenRole() : UserRole {
        return userRole
    }

    override suspend fun getToken(): String {
        return TOKEN
    }

    override fun logout() {

    }
}