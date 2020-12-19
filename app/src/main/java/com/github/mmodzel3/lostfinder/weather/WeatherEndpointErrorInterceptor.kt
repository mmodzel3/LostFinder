package com.github.mmodzel3.lostfinder.weather

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.net.ConnectException
import java.net.SocketTimeoutException

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
        } catch (e: SocketTimeoutException) {
            throw WeatherEndpointAccessErrorException()
        } catch (e: ConnectException) {
            throw WeatherEndpointAccessErrorException()
        }
    }
}