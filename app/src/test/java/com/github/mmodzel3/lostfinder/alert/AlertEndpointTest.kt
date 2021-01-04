package com.github.mmodzel3.lostfinder.alert

import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class AlertEndpointTest : AlertEndpointTestAbstract() {
    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun whenErrorOnEndpointThenGotChatEndpointAccessErrorException() {
        mockServerFailureResponse()

        assertThrows<AlertEndpointAccessErrorException> {
            runBlocking {
                alertEndpoint.getAllActiveAlerts()
            }
        }
    }
}