package com.github.mmodzel3.lostfinder.security.authentication.register

import com.github.mmodzel3.lostfinder.server.ServerEndpointTestAbstract
import org.junit.Before

abstract class RegisterEndpointTestAbstract : ServerEndpointTestAbstract() {
    protected lateinit var registerEndpoint: RegisterEndpoint

    @Before
    override fun setUp() {
        super.setUp()
        registerEndpoint = RegisterEndpointFactory.createRegisterEndpoint()
    }

    fun mockServerRegisterResponse() {
        mockServerJsonResponse(Any())
    }
}