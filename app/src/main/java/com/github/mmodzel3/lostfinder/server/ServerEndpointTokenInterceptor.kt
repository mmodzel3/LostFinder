package com.github.mmodzel3.lostfinder.server

import com.github.mmodzel3.lostfinder.security.authentication.token.TokenAuthServiceBinder
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

class ServerEndpointTokenInterceptor(private val tokenAuthServiceBinder: TokenAuthServiceBinder) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val token: String = tokenAuthServiceBinder.getToken()

        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return@runBlocking chain.proceed(newRequest)
    }
}