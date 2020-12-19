package com.github.mmodzel3.lostfinder.weather

import com.github.mmodzel3.lostfinder.R
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class WeatherConditionConverterTest {
    companion object {
        const val CONDITION_ID_IN_MAP = 200
        const val CONDITION_ID_NOT_IN_MAP = 299
        const val CONDITION_ICON_ID = "01d"
    }

    @Test
    fun whenConvertWeatherConditionToDescriptionStringIdWithConditionInMapThenGotCorrectValue() {
        val weatherCondition = WeatherCondition(CONDITION_ID_IN_MAP, CONDITION_ICON_ID)
        val descId: Int = WeatherConditionConverter.convertWeatherConditionToDescriptionStringId(weatherCondition)

        assertThat(descId).isEqualTo(R.string.fragment_weather_thunderstorm_with_light_rain)
    }

    @Test
    fun whenConvertWeatherConditionToDescriptionStringIdWithConditionNotInMapThenGroupIdIsReturned() {
        val weatherCondition = WeatherCondition(CONDITION_ID_NOT_IN_MAP, CONDITION_ICON_ID)
        val descId: Int = WeatherConditionConverter.convertWeatherConditionToDescriptionStringId(weatherCondition)

        assertThat(descId).isEqualTo(R.string.fragment_weather_thunderstorm_with_light_rain)
    }

    @Test
    fun whenConvertWeatherConditionToIconIdThenCorrectIdIsReturned() {
        val weatherCondition = WeatherCondition(CONDITION_ID_NOT_IN_MAP, CONDITION_ICON_ID)
        val iconId: Int = WeatherConditionConverter.convertWeatherConditionToIconId(weatherCondition)

        assertThat(iconId).isEqualTo(R.drawable.ic_sun)
    }
}