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
    suspend fun getUsers(@Query("all") all: Boolean = false): List<User>

    @POST("/api/user/location")
    suspend fun updateUserLocation(@Body location: Location): ServerResponse

    @POST("/api/user/location/clear")
    suspend fun clearUserLocation(): ServerResponse

    @POST("/api/user/notification/token")
    suspend fun updateUserNotificationDestToken(@Query("token") token: String): ServerResponse

    @POST("/api/user/password")
    suspend fun updateUserPassword(@Query("oldPassword") oldPassword: String,
                                   @Query("newPassword") newPassword: String): ServerResponse

    @POST("/api/user/role")
    suspend fun updateUserRole(@Query("userEmail") email: String,
                                   @Query("role") role: UserRole): ServerResponse

    @POST("/api/user/block")
    suspend fun updateUserBlock(@Query("userEmail") email: String,
                               @Query("isBlocked") isBlocked: Boolean): ServerResponse

    @POST("/api/user/delete")
    suspend fun deleteUser(@Query("userEmail") email: String = ""): ServerResponse
}