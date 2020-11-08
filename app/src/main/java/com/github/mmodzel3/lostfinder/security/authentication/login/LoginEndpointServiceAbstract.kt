package com.github.mmodzel3.lostfinder.security.authentication.login

import com.github.mmodzel3.lostfinder.server.ServerEndpointServiceAbstract
import retrofit2.Response

abstract class LoginEndpointServiceAbstract : ServerEndpointServiceAbstract() {
    private val loginEndpoint = createEndpoint<LoginEndpoint>()

    fun sendLoginRequestAndGetToken(emailAddress: String, password: String): String {
        val loginCall = loginEndpoint.login(emailAddress, password)
        val response = loginCall.execute()

        if (response.isSuccessful) {
            return extractTokenFromResponse(response)
        } else {
            throw LoginAccessErrorException()
        }
    }

    private fun extractTokenFromResponse(response: Response<String>) : String {
        val token: String? = response.body()

        return if (!token.isNullOrEmpty()) {
            token
        } else {
            throw LoginInvalidCredentialsException()
        }
    }
}