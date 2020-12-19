package com.github.mmodzel3.lostfinder.weather

import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.github.mmodzel3.lostfinder.R
import com.google.common.truth.Truth.assertThat
import okhttp3.mockwebserver.RecordedRequest
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class WeatherActivityTest : WeatherEndpointTestAbstract() {
    private lateinit var weatherScenario: ActivityScenario<WeatherActivity>
    private lateinit var decorView: View

    @Before
    override fun setUp() {
        super.setUp()

        weatherScenario = ActivityScenario.launch(WeatherActivity::class.java)

        weatherScenario.onActivity {
            decorView = it.window.decorView
        }
    }

    @Test
    fun whenGotLocationThenDataIsFetched() {
        mockGetWeatherForecastResponse(createTestWeatherForecast())

        weatherScenario.onActivity {
            it.onLocationChange(LATITUDE, LONGITUDE)
        }

        val recordedRequest: RecordedRequest? = server.takeRequest(2000, TimeUnit.MILLISECONDS)
        assertThat(recordedRequest).isNotNull()

        Thread.sleep(2000)
    }

    @Test
    fun whenGotLocationThenDataIsFetchedAndMsgToastIsShown() {
        mockGetWeatherForecastResponse(createTestWeatherForecast())

        weatherScenario.onActivity {
            it.onLocationChange(LATITUDE, LONGITUDE)
        }

        server.takeRequest(2000, TimeUnit.MILLISECONDS)

        Thread.sleep(1000)
        onView(withText(R.string.activity_weather_msg_fetching))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }

    @Test
    fun whenGotLocationAndProblemWithConnectionToApiThenErrorToastIsShown() {
        mockServerFailureResponse()

        weatherScenario.onActivity {
            it.onLocationChange(LATITUDE, LONGITUDE)
        }

        Thread.sleep(3000)
        onView(withText(R.string.activity_weather_err_api_access_error))
                .inRoot(withDecorView(Matchers.not(decorView)))
                .check(matches(isDisplayed()));

        Thread.sleep(2000)
    }
}