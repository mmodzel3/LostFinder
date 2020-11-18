package com.github.mmodzel3.lostfinder.user

import com.github.mmodzel3.lostfinder.server.ServerEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.server.ServerEndpointErrorInterceptor
import okhttp3.Interceptor
import okhttp3.Response

class UserEndpointErrorInterceptor : ServerEndpointErrorInterceptor() {

    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            return super.intercept(chain)
        } catch (e: ServerEndpointAccessErrorException) {
            throw UserEndpointAccessErrorException()
        }
    }
}
