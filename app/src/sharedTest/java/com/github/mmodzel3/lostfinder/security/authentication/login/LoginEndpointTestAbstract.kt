package com.github.mmodzel3.lostfinder.security.authentication.login

import com.github.mmodzel3.lostfinder.server.ServerEndpointTestAbstract
import com.github.mmodzel3.lostfinder.user.UserRole
import org.junit.Before

abstract class LoginEndpointTestAbstract : ServerEndpointTestAbstract() {
    companion object {
        const val TOKEN = "token1.token2.token3"
        const val EMAIL = "example@example.com"
        const val USERNAME = "username"
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

    private fun mockServerLoginInfoResponse(token: String, email: String = EMAIL,
                                            username: String = USERNAME,
                                            role: UserRole = UserRole.OWNER) {
        val loginInfo = LoginInfo(token, email, username, role, false)
        mockServerJsonResponse(loginInfo)
    }
}