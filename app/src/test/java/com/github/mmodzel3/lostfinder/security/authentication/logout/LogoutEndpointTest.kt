package com.github.mmodzel3.lostfinder.security.authentication.logout

import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class LogoutEndpointTest : LogoutEndpointTestAbstract() {
    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun whenErrorOnEndpointThenGotUserEndpointAccessErrorException() {
        mockServerFailureResponse()

        assertThrows<LogoutEndpointAccessErrorException> {
            runBlocking {
                logoutEndpoint.logout()
            }
        }
    }
}