package com.github.mmodzel3.lostfinder.user

import com.github.mmodzel3.lostfinder.server.ServerEndpointTestAbstract
import com.github.mmodzel3.lostfinder.server.ServerResponse
import java.util.*

abstract class UserEndpointTestAbstract : ServerEndpointTestAbstract() {
    companion object {
        const val USER_ID = "123456"
        const val USER_EMAIL = "example@example.com"
        const val USER_NAME = "example"
        val USER_ROLE = UserRole.MANAGER
        const val DAY_BEFORE_IN_MILLISECONDS = 24*60*60*1000
    }

    protected lateinit var userEndpoint: UserEndpoint
    protected var users: List<User> = emptyList()

    override fun setUp() {
        super.setUp()
        createTestUsers()

        userEndpoint = UserEndpointFactory.createUserEndpoint(null)
    }

    fun mockGetAllUsersResponse() {
        mockServerJsonResponse(users)
    }

    fun mockUpdateUserLocationResponse() {
        mockServerJsonResponse(ServerResponse.OK)
    }

    fun mockSuccessChangePasswordResponse() {
        mockServerJsonResponse(ServerResponse.OK)
    }

    fun mockInvalidOldPasswordChangePasswordResponse() {
        mockServerJsonResponse(ServerResponse.INVALID_PARAM)
    }

    protected fun createTestUsers() {
        val yesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS)
        val user = User(USER_ID, USER_EMAIL, null, USER_NAME, USER_ROLE, null, yesterday, null)
        users = listOf(user)
    }
}