package com.github.mmodzel3.lostfinder.server

import com.github.mmodzel3.lostfinder.ThrownExceptionTestAbstract
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before

abstract class ServerEndpointTestAbstract : ThrownExceptionTestAbstract() {
    protected lateinit var server: MockWebServer
    protected lateinit var gson: Gson

    @Before
    open fun setUp() {
        gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
            .create()

        server = MockWebServer()
        server.start()
        setServerUrl("/")
    }

    @After
    open fun tearDown() {
        server.shutdown()
    }

    fun setServerUrl(url: String) {
        ServerEndpointFactory.SERVER_URL = server.url(url).toString()
    }

    fun mockServerJsonResponse(obj: Any) {
        val json: String = gson.toJson(obj)

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

    fun mockInvalidCredentialsResponse() {
        server.enqueue(
            MockResponse()
                .setResponseCode(401))
    }
}