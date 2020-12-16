package com.github.mmodzel3.lostfinder.alert

import com.github.mmodzel3.lostfinder.server.ServerEndpointAccessErrorException
import com.github.mmodzel3.lostfinder.server.ServerEndpointErrorInterceptor
import okhttp3.Interceptor
import okhttp3.Response

class AlertEndpointErrorInterceptor : ServerEndpointErrorInterceptor() {

    override fun intercept(chain: Interceptor.Chain): Response {
        try {
            return super.intercept(chain)
        } catch (e: ServerEndpointAccessErrorException) {
            throw AlertEndpointAccessErrorException()
        }
    }
}
