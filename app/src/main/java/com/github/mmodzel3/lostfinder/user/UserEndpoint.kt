package com.github.mmodzel3.lostfinder.user

import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.server.ServerEndpointInterface
import com.github.mmodzel3.lostfinder.server.ServerResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserEndpoint : ServerEndpointInterface {
    @GET("/api/users")
    suspend fun getAllUsers(): List<User>

    @POST("/api/user/location")
    suspend fun updateUserLocation(@Body location: Location): ServerResponse

    @POST("/api/user/notification/token")
    suspend fun updateUserNotificationDestToken(@Query("token") token: String): ServerResponse

    @POST("/api/user/password")
    suspend fun updateUserPassword(@Query("oldPassword") oldPassword: String,
                                   @Query("newPassword") newPassword: String): ServerResponse
}