package com.github.mmodzel3.lostfinder.security.authentication.login

import kotlinx.coroutines.runBlocking
import org.junit.Test

class LoginEndpointTest : LoginEndpointTestAbstract() {
    companion object {
        private const val EMAIL_ADDRESS = "example@example.com"
        private const val PASSWORD = "password"
        private const val NOTIFICATION_DEST_TOKEN = "notification_token"
    }

    @Test
    fun whenErrorOnEndpointThenGotLoginEndpointAccessErrorException() {
        mockServerFailureResponse()

        assertThrows<LoginEndpointAccessErrorException> {
            runBlocking {
                loginEndpoint.login(EMAIL_ADDRESS, PASSWORD, NOTIFICATION_DEST_TOKEN)
            }
        }
    }
}