package com.github.mmodzel3.lostfinder.alert

import com.github.mmodzel3.lostfinder.server.ServerEndpointInterface
import retrofit2.http.*


interface AlertEndpoint : ServerEndpointInterface {
    @GET("/api/alerts")
    suspend fun getAllActiveAlerts(): List<Alert>

    @GET("/api/alerts/add")
    suspend fun addAlert(@Body userAlert: UserAlert): Alert

    @PUT("/api/alerts/end")
    suspend fun endAlert(@Query("alertId") alertId: String): Alert
}
