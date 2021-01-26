package com.github.mmodzel3.lostfinder.chat

import com.github.mmodzel3.lostfinder.server.ServerEndpointInterface
import com.github.mmodzel3.lostfinder.server.ServerResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ChatEndpoint : ServerEndpointInterface {
    @GET("/api/chat")
    suspend fun getMessages(@Query("page") page: Int,
                            @Query("pageSize") pageSize: Int): List<ChatMessage>

    @POST("/api/chat")
    suspend fun sendMessage(@Body chatUserMessage: ChatUserMessage): ServerResponse
}
