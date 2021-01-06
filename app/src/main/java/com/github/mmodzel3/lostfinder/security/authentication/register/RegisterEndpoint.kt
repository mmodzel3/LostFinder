package com.github.mmodzel3.lostfinder.security.authentication.register

import com.github.mmodzel3.lostfinder.server.ServerEndpointInterface
import com.github.mmodzel3.lostfinder.server.ServerResponse
import retrofit2.http.POST
import retrofit2.http.Query


interface RegisterEndpoint : ServerEndpointInterface {
    @POST("/register")
    suspend fun register(@Query("email") emailAddress: String,
                         @Query("password") password : String,
                         @Query("serverPassword") serverPassword : String = "",
                         @Query("username") username: String): ServerResponse
}
