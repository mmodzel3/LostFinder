package com.github.mmodzel3.lostfinder.weather

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.MutableLiveData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.weather.WeatherFragment.Companion.WEATHER_TOMORROW_TYPE
import org.junit.Before
import org.junit.Test

class WeatherFragmentTest : WeatherRepositoryTestAbstract() {
    private lateinit var fragmentScenario: FragmentScenario<WeatherFragment>
    private lateinit var weatherData: MutableLiveData<Weather>

    @Before
    override fun setUp() {
        super.setUp()

        weatherData = MutableLiveData()

        fragmentScenario = launchFragmentInContainer {
            WeatherFragment.create(WEATHER_TOMORROW_TYPE)
        }
    }

    @Test
    fun whenUpdateWeatherDataThenFragmentIsUpdatedWithCorrectData() {
        var typeName = ""
        val weather: Weather = createTestWeatherCurrent().convertToWeather()

        fragmentScenario.onFragment(object : FragmentScenario.FragmentAction<WeatherFragment> {
            override fun perform(fragment: WeatherFragment) {
                val weatherViewModel: WeatherViewModel = fragment.weatherViewModel
                weatherViewModel.tomorrow.value = weather

                typeName = fragment.getString(R.string.fragment_weather_tomorrow_full)
            }
        })

        Thread.sleep(1000)

        onView(ViewMatchers.withId(R.id.fragment_weather_tv_type_name))
                .check(matches(withText(typeName)))

        onView(ViewMatchers.withId(R.id.fragment_weather_tv_temperature))
                .check(matches(withText(weather.temperature.toString())))

        onView(ViewMatchers.withId(R.id.fragment_weather_tv_pressure))
                .check(matches(withText(weather.pressure.toString())))

        onView(ViewMatchers.withId(R.id.fragment_weather_tv_humidity))
                .check(matches(withText(weather.humidity.toString())))

        onView(ViewMatchers.withId(R.id.fragment_weather_tv_clouds))
                .check(matches(withText(weather.clouds.toString())))

        onView(ViewMatchers.withId(R.id.fragment_weather_tv_visibility))
                .check(matches(withText(weather.visibility.toString())))

        onView(ViewMatchers.withId(R.id.fragment_weather_tv_wind_speed))
                .check(matches(withText(weather.windSpeed.toString())))

        onView(ViewMatchers.withId(R.id.fragment_weather_tv_wind_degree))
                .check(matches(withText(weather.windDegree.toString())))
    }
}