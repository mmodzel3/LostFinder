package com.github.mmodzel3.lostfinder.security.authentication.logout

import com.github.mmodzel3.lostfinder.server.ServerEndpointInterface
import com.github.mmodzel3.lostfinder.server.ServerResponse
import retrofit2.http.POST

interface LogoutEndpoint : ServerEndpointInterface {
    @POST("/api/logout")
    suspend fun logout(): ServerResponse
}
