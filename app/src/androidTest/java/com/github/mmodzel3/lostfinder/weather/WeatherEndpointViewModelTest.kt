package com.github.mmodzel3.lostfinder.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class WeatherEndpointViewModelTest : WeatherEndpointTestAbstract() {
    private lateinit var weatherEndpointViewModel: WeatherEndpointViewModel
    private lateinit var latch: CountDownLatch

    @Before
    override fun setUp() {
        super.setUp()

        weatherEndpointViewModel = WeatherEndpointViewModel(weatherEndpoint, WEATHER_API_KEY, WEATHER_UNITS)
        latch = CountDownLatch(1)
    }

    @Test
    fun whenFetchDataThenNowDataIsUpdated() {
        val weatherForecast: WeatherForecast = prepareFetchWeatherData()

        assertThat(weatherEndpointViewModel.now.value).isEqualTo(weatherForecast.now.convertToWeather())
    }

    @Test
    fun whenFetchDataThenNextHourDataIsUpdated() {
        val weatherForecast: WeatherForecast = prepareFetchWeatherData()

        assertThat(weatherEndpointViewModel.nextHour.value).isEqualTo(weatherForecast.hourly[0].convertToWeather())
    }

    @Test
    fun whenFetchDataThenTodayDataIsUpdated() {
        val weatherForecast: WeatherForecast = prepareFetchWeatherData()

        assertThat(weatherEndpointViewModel.today.value).isEqualTo(weatherForecast.daily[0].convertToWeather())
    }

    @Test
    fun whenFetchDataThenTomorrowDataIsUpdated() {
        val weatherForecast: WeatherForecast = prepareFetchWeatherData()

        assertThat(weatherEndpointViewModel.tomorrow.value).isEqualTo(weatherForecast.daily[1].convertToWeather())
    }

    private fun prepareFetchWeatherData(): WeatherForecast {
        val weatherForecast: WeatherForecast = createTestWeatherForecast()
        mockGetWeatherForecastResponse(weatherForecast)

        observeAndWaitForStatusChange(weatherEndpointViewModel.now) {
            runBlocking {
                weatherEndpointViewModel.fetchData(LATITUDE, LONGITUDE)
            }
        }

        return weatherForecast
    }

    private fun <T> observeAndWaitForStatusChange(data: MutableLiveData<T>, doAfterObserving: () -> Unit) {
        val observer = Observer<T> {
            latch.countDown()
        }

        runBlocking(Dispatchers.Main) {
            data.observeForever(observer)
        }

        doAfterObserving()
        latch.await(2000, TimeUnit.MILLISECONDS)

        runBlocking(Dispatchers.Main) {
            data.removeObserver(observer)
        }
    }
}