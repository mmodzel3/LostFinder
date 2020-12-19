package com.github.mmodzel3.lostfinder.weather

import com.google.gson.Gson
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before

abstract class WeatherEndpointTestAbstract {
    companion object {
        const val WEATHER_TEMPERATURE = 20.0
        const val WEATHER_PRESSURE = 1024
        const val WEATHER_CLOUDS = 40
        const val WEATHER_HUMIDITY = 10
        const val WEATHER_VISIBILITY = 10000
        const val WEATHER_WIND_SPEED = 10.0
        const val WEATHER_WIND_DEGREE = 279.0

        const val WEATHER_CLEAR_SKY = 800
        const val WEATHER_CLEAR_SKY_ICON_DAY = "01d"

        const val WEATHER_API_KEY = "api_key"
        const val LONGITUDE = 51.5
        const val LATITUDE = 20.5
        const val WEATHER_UNITS = "standard"

        const val SECONDS_IN_HOUR = 60 * 60
        const val SECONDS_IN_DAY = 24 * SECONDS_IN_HOUR
    }

    protected lateinit var server: MockWebServer
    protected lateinit var weatherEndpoint: WeatherEndpoint

    @Before
    open fun setUp() {
        server = MockWebServer()
        server.start()
        setServerUrl("/")

        weatherEndpoint = WeatherEndpointFactory.createWeatherEndpoint()
    }

    @After
    open fun tearDown() {
        server.shutdown()
    }

    private fun setServerUrl(url: String) {
        WeatherEndpointFactory.WEATHER_API_URL = server.url(url).toString()
    }

    fun mockServerJsonResponse(obj: Any) {
        val json: String = Gson().toJson(obj)

        server.enqueue(MockResponse()
                        .setResponseCode(200)
                        .setBody(json))
    }

    fun mockServerFailureResponse() {
        server.enqueue(MockResponse().setResponseCode(500))
    }

    fun mockGetWeatherForecastResponse(weatherForecast: WeatherForecast) {
        mockServerJsonResponse(weatherForecast)
    }

    protected fun createTestWeatherForecast(): WeatherForecast {
        return WeatherForecast(createTestWeatherCurrent(),
                               createTestWeatherHourlyForecast(),
                               createTestWeatherDailyForecast())
    }

    protected fun createTestWeatherCurrent() : WeatherCurrent {
        val currentTimestamp: Long = System.currentTimeMillis() / 1000

        return WeatherCurrent(currentTimestamp, WEATHER_TEMPERATURE, WEATHER_PRESSURE, WEATHER_HUMIDITY,
                WEATHER_CLOUDS, WEATHER_VISIBILITY, WEATHER_WIND_SPEED, WEATHER_WIND_DEGREE,
                listOf(WeatherCondition(WEATHER_CLEAR_SKY, WEATHER_CLEAR_SKY_ICON_DAY)))
    }

    protected fun createTestWeatherHourlyForecast() : List<WeatherHour> {
        val currentTimestamp: Long = System.currentTimeMillis() / 1000
        val nextHourTimestamp: Long = currentTimestamp - (currentTimestamp % SECONDS_IN_HOUR) + SECONDS_IN_HOUR

        return listOf(WeatherHour(nextHourTimestamp, WEATHER_TEMPERATURE, WEATHER_PRESSURE, WEATHER_HUMIDITY,
                WEATHER_CLOUDS, WEATHER_VISIBILITY, WEATHER_WIND_SPEED, WEATHER_WIND_DEGREE,
                listOf(WeatherCondition(WEATHER_CLEAR_SKY, WEATHER_CLEAR_SKY_ICON_DAY))))
    }

    protected fun createTestWeatherDailyForecast() : List<WeatherDay> {
        val currentTimestamp: Long = System.currentTimeMillis() / 1000
        val tomorrowTimestamp: Long = currentTimestamp - (currentTimestamp % SECONDS_IN_DAY) + SECONDS_IN_DAY

        val todayWeather = WeatherDay(currentTimestamp, WeatherTemperatures(WEATHER_TEMPERATURE),
                WEATHER_PRESSURE, WEATHER_HUMIDITY, WEATHER_CLOUDS, WEATHER_VISIBILITY,
                WEATHER_WIND_SPEED, WEATHER_WIND_DEGREE,
                listOf(WeatherCondition(WEATHER_CLEAR_SKY, WEATHER_CLEAR_SKY_ICON_DAY)))

        val tomorrowWeather = WeatherDay(tomorrowTimestamp, WeatherTemperatures(WEATHER_TEMPERATURE),
                WEATHER_PRESSURE, WEATHER_HUMIDITY, WEATHER_CLOUDS, WEATHER_VISIBILITY,
                WEATHER_WIND_SPEED, WEATHER_WIND_DEGREE,
                listOf(WeatherCondition(WEATHER_CLEAR_SKY, WEATHER_CLEAR_SKY_ICON_DAY)))

        return listOf(todayWeather, tomorrowWeather)
    }
}