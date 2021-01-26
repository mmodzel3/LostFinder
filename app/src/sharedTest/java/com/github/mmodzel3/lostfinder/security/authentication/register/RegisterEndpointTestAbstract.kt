package com.github.mmodzel3.lostfinder.security.authentication.register

import com.github.mmodzel3.lostfinder.server.ServerEndpointTestAbstract
import com.github.mmodzel3.lostfinder.server.ServerResponse
import org.junit.Before

abstract class RegisterEndpointTestAbstract : ServerEndpointTestAbstract() {
    protected lateinit var registerEndpoint: RegisterEndpoint

    @Before
    override fun setUp() {
        super.setUp()
        registerEndpoint = RegisterEndpointFactory.createRegisterEndpoint()
    }

    fun mockServerRegisterResponse() {
        mockServerJsonResponse(ServerResponse.OK)
    }

    fun mockServerDuplicatedResponse() {
        mockServerJsonResponse(ServerResponse.DUPLICATED)
    }

    fun mockServerInvalidPasswordResponse() {
        mockServerJsonResponse(ServerResponse.INVALID_PERMISSION)
    }

    fun mockPasswordTooShortResponse() {
        mockServerJsonResponse(ServerResponse.INVALID_PARAM)
    }
}