package com.github.mmodzel3.lostfinder.alert

import com.github.mmodzel3.lostfinder.server.ServerEndpointInterface
import com.github.mmodzel3.lostfinder.server.ServerResponse
import retrofit2.http.*


interface AlertEndpoint : ServerEndpointInterface {
    @GET("/api/alerts")
    suspend fun getAllActiveAlerts(): List<Alert>

    @POST("/api/alerts/add")
    suspend fun addAlert(@Body userAlert: UserAlert): ServerResponse

    @PUT("/api/alerts/end")
    suspend fun endAlert(@Query("alertId") alertId: String): ServerResponse
}
