package com.github.mmodzel3.lostfinder.user

import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.server.ServerEndpointInterface
import com.github.mmodzel3.lostfinder.server.ServerResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserEndpoint : ServerEndpointInterface {
    @GET("/api/users")
    suspend fun getUsers(): List<User>

    @POST("/api/user/location")
    suspend fun updateUserLocation(@Body location: Location): ServerResponse
}