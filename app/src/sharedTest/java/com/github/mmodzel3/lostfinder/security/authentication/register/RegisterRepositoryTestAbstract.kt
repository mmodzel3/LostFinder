package com.github.mmodzel3.lostfinder.security.authentication.register

import org.junit.Before

abstract class RegisterRepositoryTestAbstract : RegisterEndpointTestAbstract() {
    protected lateinit var registerRepository: RegisterRepository

    @Before
    override fun setUp() {
        super.setUp()
        registerRepository = RegisterRepository.getInstance()
    }
}