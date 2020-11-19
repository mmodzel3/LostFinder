package com.github.mmodzel3.lostfinder.user

import android.content.Context
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerEndpointTestAbstract
import com.github.mmodzel3.lostfinder.server.ServerResponse
import org.junit.Before
import java.util.*

abstract class UserEndpointTestAbstract : ServerEndpointTestAbstract() {
    companion object {
        const val USER_ID = "123456"
        const val USER_EMAIL = "example@example.com"
        const val USER_NAME = "example"
        const val USER_ROLE = "ADMIN"
        const val DAY_BEFORE_IN_MILLISECONDS = 24*60*60*1000
    }

    protected lateinit var userEndpoint: UserEndpoint

    fun setUp(tokenManager: TokenManager) {
        super.setUp()
        userEndpoint = UserEndpointFactory.createUserEndpoint(tokenManager)
    }

    fun mockGetAllUsersResponse() {
        val yesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS)
        val user = User(USER_ID, USER_EMAIL, null, USER_NAME, USER_ROLE, null, yesterday)
        val users: List<User> = listOf(user)
        mockServerJsonResponse(users)
    }

    fun mockUpdateUserLocationResponse() {
        mockServerJsonResponse(ServerResponse.OK)
    }
}