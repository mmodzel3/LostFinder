package com.github.mmodzel3.lostfinder.security.authentication.logout

import com.github.mmodzel3.lostfinder.security.authentication.login.LoginEndpoint
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginEndpointFactory
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginInfo
import com.github.mmodzel3.lostfinder.server.ServerEndpointTestAbstract
import com.github.mmodzel3.lostfinder.user.UserRole
import org.junit.Before

abstract class LogoutEndpointTestAbstract : ServerEndpointTestAbstract() {
    protected lateinit var logoutEndpoint: LogoutEndpoint

    @Before
    override fun setUp() {
        super.setUp()
        logoutEndpoint = LogoutEndpointFactory.createLogoutEndpoint(null)
    }
}