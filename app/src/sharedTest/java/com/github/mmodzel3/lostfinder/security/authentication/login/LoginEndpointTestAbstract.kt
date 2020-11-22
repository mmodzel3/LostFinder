package com.github.mmodzel3.lostfinder.security.authentication.login

import com.github.mmodzel3.lostfinder.security.authentication.login.LoginEndpoint
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginEndpointFactory
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginInfo
import com.github.mmodzel3.lostfinder.server.ServerEndpointTestAbstract
import org.junit.Before

abstract class LoginEndpointTestAbstract : ServerEndpointTestAbstract() {
    companion object {
        const val TOKEN = "token1.token2.token3"
    }

    protected lateinit var loginEndpoint: LoginEndpoint

    @Before
    override fun setUp() {
        super.setUp()
        loginEndpoint = LoginEndpointFactory.createLoginEndpoint()
    }

    fun mockServerTokenResponse() {
        mockServerLoginInfoResponse(TOKEN)
    }

    fun mockServerInvalidCredentialsResponse() {
        mockServerLoginInfoResponse("")
    }

    private fun mockServerLoginInfoResponse(token: String) {
        val loginInfo = LoginInfo(token)
        mockServerJsonResponse(loginInfo)
    }
}