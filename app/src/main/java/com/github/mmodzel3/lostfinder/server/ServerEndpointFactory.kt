package com.github.mmodzel3.lostfinder.server

import com.github.mmodzel3.lostfinder.security.authentication.token.TokenAuthServiceBinder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServerEndpointFactory {
    var SERVER_URL = "http://localhost:8080/"

    inline fun <reified T: ServerEndpointInterface>
            createServerEndpoint(errorInterceptor: ServerEndpointErrorInterceptor): T {
        return createRetrofit(errorInterceptor).create(T::class.java)
    }

    inline fun <reified T: ServerEndpointInterface>
            createServerEndpoint(authTokenAuthServiceBinder: TokenAuthServiceBinder,
                                 errorInterceptor: ServerEndpointErrorInterceptor): T {
        return createRetrofit(authTokenAuthServiceBinder, errorInterceptor).create(T::class.java)
    }

    private fun createClient(errorInterceptor: ServerEndpointErrorInterceptor) : OkHttpClient {
        return OkHttpClient.Builder()
                .addInterceptor(errorInterceptor)
                .build()
    }

    private fun createClient(authTokenAuthServiceBinder: TokenAuthServiceBinder,
                             errorInterceptor: ServerEndpointErrorInterceptor) : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(ServerEndpointTokenInterceptor(authTokenAuthServiceBinder))
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

    fun createRetrofit(authTokenAuthServiceBinder: TokenAuthServiceBinder,
                       errorInterceptor: ServerEndpointErrorInterceptor) : Retrofit {
        return Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(createClient(authTokenAuthServiceBinder, errorInterceptor))
            .build()
    }
}