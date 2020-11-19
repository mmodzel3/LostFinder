package com.github.mmodzel3.lostfinder.authentication.login

import com.github.mmodzel3.lostfinder.authentication.token.TokenManagerStub
import com.github.mmodzel3.lostfinder.user.UserEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.user.UserEndpointTestAbstract
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class UserEndpointTest : UserEndpointTestAbstract() {
    @Before
    override fun setUp() {
        super.setUp(TokenManagerStub.getInstance())
    }

    @Test
    fun whenErrorOnEndpointThenGotUserEndpointAccessErrorException() {
        mockServerFailureResponse()

        assertThrows<UserEndpointAccessErrorException> {
            runBlocking {
                userEndpoint.getAllUsers()
            }
        }
    }
}