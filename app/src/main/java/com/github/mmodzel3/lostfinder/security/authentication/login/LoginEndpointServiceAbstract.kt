package com.github.mmodzel3.lostfinder.security.authentication.login

import com.github.mmodzel3.lostfinder.server.ServerEndpointServiceAbstract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

abstract class LoginEndpointServiceAbstract : ServerEndpointServiceAbstract() {
    private val loginEndpoint = createEndpoint<LoginEndpoint>()

    protected suspend fun retrieveLoginInfoAndCheckToken(emailAddress: String, password: String): LoginInfo {
        val loginInfo: LoginInfo = retrieveLoginInfo(emailAddress, password)

        if (loginInfo.token.isNotEmpty()) {
            return loginInfo
        } else {
            throw LoginInvalidCredentialsException()
        }
    }

    private suspend fun retrieveLoginInfo(emailAddress: String, password: String): LoginInfo {
        try {
            return withContext(Dispatchers.IO)
            { loginEndpoint.login(emailAddress, password) }

        } catch (e: IOException) {
            throw LoginEndpointAccessErrorException()
        }
    }
}
