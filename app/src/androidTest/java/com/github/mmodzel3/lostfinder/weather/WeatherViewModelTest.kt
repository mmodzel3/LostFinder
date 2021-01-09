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

class WeatherViewModelTest : WeatherRepositoryTestAbstract() {
    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var latch: CountDownLatch

    @Before
    override fun setUp() {
        super.setUp()

        weatherViewModel = WeatherViewModel(WEATHER_API_KEY, WEATHER_UNITS)
        latch = CountDownLatch(1)
    }

    @Test
    fun whenFetchDataThenNowDataIsUpdated() {
        val weatherForecast: WeatherForecast = prepareFetchWeatherData()

        assertThat(weatherViewModel.now.value).isEqualTo(weatherForecast.now.convertToWeather())
    }

    @Test
    fun whenFetchDataThenNextHourDataIsUpdated() {
        val weatherForecast: WeatherForecast = prepareFetchWeatherData()

        assertThat(weatherViewModel.nextHour.value).isEqualTo(weatherForecast.hourly[0].convertToWeather())
    }

    @Test
    fun whenFetchDataThenTodayDataIsUpdated() {
        val weatherForecast: WeatherForecast = prepareFetchWeatherData()

        assertThat(weatherViewModel.today.value).isEqualTo(weatherForecast.daily[0].convertToWeather())
    }

    @Test
    fun whenFetchDataThenTomorrowDataIsUpdated() {
        val weatherForecast: WeatherForecast = prepareFetchWeatherData()

        assertThat(weatherViewModel.tomorrow.value).isEqualTo(weatherForecast.daily[1].convertToWeather())
    }

    private fun prepareFetchWeatherData(): WeatherForecast {
        val weatherForecast: WeatherForecast = createTestWeatherForecast()
        mockGetWeatherForecastResponse(weatherForecast)

        observeAndWaitForStatusChange(weatherViewModel.now) {
            runBlocking {
                weatherViewModel.fetchDataAndUpdate(LATITUDE, LONGITUDE)
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