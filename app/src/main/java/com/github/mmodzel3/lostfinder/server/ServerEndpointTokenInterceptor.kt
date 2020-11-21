package com.github.mmodzel3.lostfinder.server

import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class ServerEndpointTokenInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        val token: String = tokenManager.getToken()

        val newRequest = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        return@runBlocking chain.proceed(newRequest)
    }
}