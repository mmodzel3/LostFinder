package com.github.mmodzel3.lostfinder.security.authentication.logout

import org.junit.After
import org.junit.Before

abstract class LogoutRepositoryTestAbstract : LogoutEndpointTestAbstract() {
    protected lateinit var logoutRepository: LogoutRepository

    @Before
    override fun setUp() {
        super.setUp()
        logoutRepository = LogoutRepository.getInstance(null)
    }

    @After
    override fun tearDown() {
        super.tearDown()

        LogoutRepository.clear()
    }
}