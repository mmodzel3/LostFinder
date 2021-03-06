package com.github.mmodzel3.lostfinder.security.authentication.register

import kotlinx.coroutines.runBlocking
import org.junit.Test

class RegisterEndpointTest : RegisterEndpointTestAbstract() {
    companion object {
        private const val EMAIL_ADDRESS = "example@example.com"
        private const val PASSWORD = "password"
        private const val SERVER_PASSWORD = "123"
        private const val USERNAME = "user123"
    }

    @Test
    fun whenErrorOnEndpointThenGotLoginEndpointAccessErrorException() {
        mockServerFailureResponse()

        assertThrows<RegisterEndpointAccessErrorException> {
            runBlocking {
                registerEndpoint.register(EMAIL_ADDRESS, PASSWORD, SERVER_PASSWORD, USERNAME)
            }
        }
    }
}