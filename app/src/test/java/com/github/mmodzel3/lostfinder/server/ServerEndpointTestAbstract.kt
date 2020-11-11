package com.github.mmodzel3.lostfinder.server

import com.github.mmodzel3.lostfinder.ThrownExceptionTestAbstract
import okhttp3.mockwebserver.MockWebServer

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
}