package com.github.mmodzel3.lostfinder.security.authentication.logout

import org.junit.Before

abstract class LogoutRepositoryTestAbstract : LogoutEndpointTestAbstract() {
    protected lateinit var logoutRepository: LogoutRepository

    @Before
    override fun setUp() {
        super.setUp()
        logoutRepository = LogoutRepository.getInstance(null)
    }
}