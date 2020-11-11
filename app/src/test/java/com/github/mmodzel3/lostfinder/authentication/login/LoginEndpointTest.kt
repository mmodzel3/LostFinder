package com.github.mmodzel3.lostfinder.authentication.login

import com.github.mmodzel3.lostfinder.security.authentication.login.LoginEndpoint
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginEndpointFactory
import com.github.mmodzel3.lostfinder.server.ServerEndpointFactory
import com.github.mmodzel3.lostfinder.server.ServerEndpointTestAbstract
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Assert
import org.junit.Before
import org.junit.Test


class LoginEndpointTest : ServerEndpointTestAbstract() {
    companion object {
        private const val EMAIL_ADDRESS = "example@example.com"
        private const val PASSWORD = "password"
    }

    private lateinit var loginEndpoint: LoginEndpoint

    @Before
    override fun setUp() {
        super.setUp()
        loginEndpoint = LoginEndpointFactory.createLoginEndpoint()
    }

    @Test
    fun whenErrorOnEndpointThenGotLoginEndpointAccessErrorException() {
        server.enqueue(
            MockResponse()
                .setResponseCode(500))

        assertThrows<LoginEndpointAccessErrorException> {
            runBlocking {
                loginEndpoint.login(EMAIL_ADDRESS, PASSWORD)
            }
        }
    }
}