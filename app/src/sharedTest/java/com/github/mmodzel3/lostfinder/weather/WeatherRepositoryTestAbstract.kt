package com.github.mmodzel3.lostfinder.weather

import org.junit.After
import org.junit.Before

abstract class WeatherRepositoryTestAbstract : WeatherEndpointTestAbstract() {
    protected lateinit var weatherRepository: WeatherRepository

    @Before
    override fun setUp() {
        super.setUp()

        weatherRepository = WeatherRepository.getInstance(WEATHER_API_KEY, WEATHER_UNITS)
    }

    @After
    override fun tearDown() {
        super.tearDown()

        WeatherRepository.clear()
    }
}