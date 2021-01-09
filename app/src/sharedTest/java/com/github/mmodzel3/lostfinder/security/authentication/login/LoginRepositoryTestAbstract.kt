package com.github.mmodzel3.lostfinder.security.authentication.login

import org.junit.After
import org.junit.Before

abstract class LoginRepositoryTestAbstract : LoginEndpointTestAbstract() {
    protected lateinit var loginRepository: LoginRepository

    @Before
    override fun setUp() {
        super.setUp()
        loginRepository = LoginRepository.getInstance()
    }

    @After
    override fun tearDown() {
        super.tearDown()

        LoginRepository.clear()
    }
}