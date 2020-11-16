package com.github.mmodzel3.lostfinder.user

import com.github.mmodzel3.lostfinder.server.ServerEndpointInterface
import retrofit2.http.GET

interface UserEndpoint : ServerEndpointInterface {
    @GET("/api/users")
    suspend fun getUsers(): List<User>
}