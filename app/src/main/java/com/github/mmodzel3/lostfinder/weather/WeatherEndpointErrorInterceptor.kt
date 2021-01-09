package com.github.mmodzel3.lostfinder.weather

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

open class WeatherEndpointErrorInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()
        val response: Response = proceedRequest(chain, originalRequest)

        return if (response.code < 400) {
            response
        } else {
            throw WeatherEndpointAccessErrorException()
        }
    }

    private fun proceedRequest(chain: Interceptor.Chain, request: Request) : Response {
        try {
            return chain.proceed(request)
        } catch (e: Exception) {
            throw WeatherEndpointAccessErrorException()
        }
    }
}