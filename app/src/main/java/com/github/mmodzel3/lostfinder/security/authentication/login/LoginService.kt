package com.github.mmodzel3.lostfinder.security.authentication.login.service

import android.content.Intent
import android.os.IBinder
import com.github.mmodzel3.lostfinder.security.authentication.login.exceptions.LoginAccessErrorException
import com.github.mmodzel3.lostfinder.security.authentication.login.exceptions.LoginInvalidCredentialsException
import com.github.mmodzel3.lostfinder.server.ServerEndpointServiceAbstract
import retrofit2.Response

class LoginService : ServerEndpointServiceAbstract() {
    private val binder = LoginServiceBinder(this)
    private val loginEndpoint = createEndpoint<LoginEndpoint>()

    fun login(emailAddress: String, password: String): String {
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

    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }
}