package com.github.mmodzel3.lostfinder.security.authentication.login.service

import com.github.mmodzel3.lostfinder.server.ServerEndpointInterface
import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Query


interface LoginEndpoint : ServerEndpointInterface {
    @POST("/login")
    fun login(@Query("email") emailAddress: String,
                @Query("password") password : String): Call<String>
}