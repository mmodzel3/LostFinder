package com.github.mmodzel3.lostfinder.authentication.login

import com.github.mmodzel3.lostfinder.security.authentication.login.LoginEndpointAccessErrorException
import kotlinx.coroutines.runBlocking
import org.junit.Test

class LoginEndpointTest : LoginEndpointTestAbstract() {
    companion object {
        private const val EMAIL_ADDRESS = "example@example.com"
        private const val PASSWORD = "password"
    }

    @Test
    fun whenErrorOnEndpointThenGotLoginEndpointAccessErrorException() {
        mockServerFailureResponse()

        assertThrows<LoginEndpointAccessErrorException> {
            runBlocking {
                loginEndpoint.login(EMAIL_ADDRESS, PASSWORD)
            }
        }
    }
}