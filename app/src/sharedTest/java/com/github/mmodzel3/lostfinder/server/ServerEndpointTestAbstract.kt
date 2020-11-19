package com.github.mmodzel3.lostfinder.server

import com.github.mmodzel3.lostfinder.ThrownExceptionTestAbstract
import com.google.gson.Gson
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.Before

abstract class ServerEndpointTestAbstract : ThrownExceptionTestAbstract() {
    protected lateinit var server: MockWebServer

    open fun setUp() {
        server = MockWebServer()
        server.start()
        setServerUrl("/")
    }

    fun setServerUrl(url: String) {
        ServerEndpointFactory.SERVER_URL = server.url(url).toString()
    }

    fun mockServerJsonResponse(obj: Any) {
        val json: String = Gson().toJson(obj)

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(json))
    }

    fun mockServerFailureResponse() {
        server.enqueue(
            MockResponse()
                .setResponseCode(500))
    }
}