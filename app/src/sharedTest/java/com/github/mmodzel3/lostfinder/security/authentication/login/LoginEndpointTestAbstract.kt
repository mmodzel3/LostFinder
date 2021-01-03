package com.github.mmodzel3.lostfinder.security.authentication.login

import com.github.mmodzel3.lostfinder.server.ServerEndpointTestAbstract
import com.github.mmodzel3.lostfinder.user.UserRole
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

    private fun mockServerLoginInfoResponse(token: String, role: UserRole = UserRole.OWNER) {
        val loginInfo = LoginInfo(token, role, false)
        mockServerJsonResponse(loginInfo)
    }
}