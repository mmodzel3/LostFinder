package com.github.mmodzel3.lostfinder.server

import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServerEndpointFactory {
    var SERVER_URL = "http://192.168.0.100:8080/"

    inline fun <reified T: ServerEndpointInterface>
            createServerEndpoint(errorInterceptor: ServerEndpointErrorInterceptor,
                                 tokenManager: TokenManager? = null): T {
        return createRetrofit(errorInterceptor, tokenManager).create(T::class.java)
    }

    private fun createClient(errorInterceptor: ServerEndpointErrorInterceptor,
                             tokenManager: TokenManager? = null) : OkHttpClient {
        val clientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
            .addInterceptor(errorInterceptor)

        if (tokenManager != null) {
            clientBuilder.addInterceptor(ServerEndpointTokenInterceptor(tokenManager))
        }

        return clientBuilder.build()
    }

    fun createRetrofit(errorInterceptor: ServerEndpointErrorInterceptor,
                       tokenManager: TokenManager? = null) : Retrofit {
        val gson: Gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
            .create()

        return Retrofit.Builder()
                .baseUrl(SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(createClient(errorInterceptor, tokenManager))
                .build()
    }
}