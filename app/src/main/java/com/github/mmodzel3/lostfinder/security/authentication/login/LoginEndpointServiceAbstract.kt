package com.github.mmodzel3.lostfinder.security.authentication.login

import android.util.Log
import com.github.mmodzel3.lostfinder.server.ServerEndpointServiceAbstract
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

abstract class LoginEndpointServiceAbstract : ServerEndpointServiceAbstract() {
    private val loginEndpoint = createEndpoint<LoginEndpoint>()

    suspend fun sendLoginRequestAndGetToken(emailAddress: String, password: String): String {
        return sendLoginRequest(emailAddress, password).token
    }

    private suspend fun sendLoginRequest(emailAddress: String, password: String): LoginInfo {
        try {
            return withContext(Dispatchers.IO)
                { loginEndpoint.login(emailAddress, password) }

        } catch (e: Exception) {
            Log.d("LoginService", e.message ?: "Exception")
            throw e
        }
    }

    private fun extractTokenFromResponse(response: Response<LoginInfo>) : String {
        val token: String? = response.body()?.token

        return if (!token.isNullOrEmpty()) {
            token
        } else {
            throw LoginInvalidCredentialsException()
        }
    }
}