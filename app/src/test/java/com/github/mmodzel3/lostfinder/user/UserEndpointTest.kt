package com.github.mmodzel3.lostfinder.user

import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class UserEndpointTest : UserEndpointTestAbstract() {
    @Before
    override fun setUp() {
        super.setUp()
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