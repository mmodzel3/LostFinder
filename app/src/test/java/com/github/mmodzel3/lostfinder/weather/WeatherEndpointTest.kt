package com.github.mmodzel3.lostfinder.weather

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test

class WeatherEndpointTest : WeatherEndpointTestAbstract() {
    @Before
    override fun setUp() {
        super.setUp()
    }

    @Test
    fun whenErrorOnEndpointThenGotWeatherEndpointAccessErrorException() {
        mockServerFailureResponse()

        assertThrows(WeatherEndpointAccessErrorException::class.java) {
            runBlocking {
                weatherEndpoint.getWeatherForecast(LATITUDE, LONGITUDE, WEATHER_API_KEY, WEATHER_UNITS)
            }
        }
    }
}