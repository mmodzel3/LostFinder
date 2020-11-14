package com.github.mmodzel3.lostfinder.server

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServerEndpointFactory {
    var SERVER_URL = "http://192.168.0.100:8080/"

    inline fun <reified T: ServerEndpointInterface>
            createServerEndpoint(errorInterceptor: ServerEndpointErrorInterceptor): T {
        return createRetrofit(errorInterceptor).create(T::class.java)
    }

    private fun createClient(errorInterceptor: ServerEndpointErrorInterceptor) : OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(errorInterceptor)
                .build()
    }

    fun createRetrofit(errorInterceptor: ServerEndpointErrorInterceptor) : Retrofit {
        return Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(createClient(errorInterceptor))
                .build()
    }
}