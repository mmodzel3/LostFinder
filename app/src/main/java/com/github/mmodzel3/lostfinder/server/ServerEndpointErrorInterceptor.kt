package com.github.mmodzel3.lostfinder.server

import android.util.Log
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

open class ServerEndpointErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val response: Response = proceedRequest(chain, originalRequest)!!

        return if (response.code < 400) {
            response
        } else if (response.code == 401) {
            throw InvalidTokenException()
        } else {
            throw ServerEndpointAccessErrorException()
        }
    }

    private fun proceedRequest(chain: Interceptor.Chain, request: Request) : Response? {
        try {
            return chain.proceed(request)
        } catch (e: Exception) {
            throw ServerEndpointAccessErrorException()
        }
    }
}