package com.github.mmodzel3.lostfinder.server

import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody

class ServerEndpointTokenInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response = runBlocking {
        try {
            val token: String = tokenManager.getToken()

            val newRequest = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()

            return@runBlocking chain.proceed(newRequest)
        } catch (e: InvalidTokenException) {
            val contentType: MediaType = "application/json; charset=utf-8".toMediaType();
            val responseBody: ResponseBody = "".toResponseBody(contentType);

            return@runBlocking Response.Builder()
                .code(401)
                .request(chain.request())
                .protocol(Protocol.HTTP_1_1)
                .message("Invalid token.")
                .body(responseBody)
                .build()
        }
    }
}