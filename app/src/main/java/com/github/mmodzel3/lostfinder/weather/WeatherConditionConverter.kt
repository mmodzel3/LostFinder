package com.github.mmodzel3.lostfinder.weather

import android.content.Context
import android.util.Log
import com.github.mmodzel3.lostfinder.R

object WeatherConditionConverter {
    private val weatherConditionDescriptionMap: Map<Int, Int> = hashMapOf(
            200 to R.string.fragment_weather_thunderstorm_with_light_rain,
            201 to R.string.fragment_weather_thunderstorm_with_rain,
            202 to R.string.fragment_weather_thunderstorm_with_heavy_rain,
            210 to R.string.fragment_weather_light_thunderstorm,
            211 to R.string.fragment_weather_thunderstorm,
            212 to R.string.fragment_weather_heavy_thunderstorm,
            221 to R.string.fragment_weather_ragged_thunderstorm,
            230 to R.string.fragment_weather_thunderstorm_with_light_drizzle,
            231 to R.string.fragment_weather_thunderstorm_with_drizzle,
            232 to R.string.fragment_weather_thunderstorm_with_heavy_drizzle,
            300 to R.string.fragment_weather_light_intensity_drizzle_rain,
            301 to R.string.fragment_weather_drizzle,
            302 to R.string.fragment_weather_heavy_intensity_drizzle,
            310 to R.string.fragment_weather_light_intensity_drizzle_rain,
            311 to R.string.fragment_weather_drizzle_rain,
            312 to R.string.fragment_weather_heavy_intensity_drizzle_rain,
            313 to R.string.fragment_weather_shower_rain_and_drizzle,
            314 to R.string.fragment_weather_heavy_shower_rain_and_drizzle,
            321 to R.string.fragment_weather_shower_drizzle,
            500 to R.string.fragment_weather_light_rain,
            501 to R.string.fragment_weather_moderate_rain,
            502 to R.string.fragment_weather_heavy_intensity_rain,
            503 to R.string.fragment_weather_very_heavy_rain,
            504 to R.string.fragment_weather_extreme_rain,
            511 to R.string.fragment_weather_freezing_rain,
            520 to R.string.fragment_weather_light_intensity_shower_rain,
            521 to R.string.fragment_weather_shower_rain,
            522 to R.string.fragment_weather_heavy_intensity_shower_rain,
            531 to R.string.fragment_weather_ragged_shower_rain,
            600 to R.string.fragment_weather_light_snow,
            601 to R.string.fragment_weather_snow,
            602 to R.string.fragment_weather_heavy_snow,
            611 to R.string.fragment_weather_sleet,
            612 to R.string.fragment_weather_light_shower_sleet,
            613 to R.string.fragment_weather_shower_sleet,
            615 to R.string.fragment_weather_light_rain_and_snow,
            616 to R.string.fragment_weather_rain_and_snow,
            620 to R.string.fragment_weather_light_shower_snow,
            621 to R.string.fragment_weather_shower_snow,
            622 to R.string.fragment_weather_heavy_shower_snow,
            701 to R.string.fragment_weather_mist,
            711 to R.string.fragment_weather_smoke,
            721 to R.string.fragment_weather_haze,
            731 to R.string.fragment_weather_sand_dust_whirls,
            751 to R.string.fragment_weather_sand,
            761 to R.string.fragment_weather_dust,
            762 to R.string.fragment_weather_volcanic_ash,
            781 to R.string.fragment_weather_tornado,
            800 to R.string.fragment_weather_clear_sky,
            801 to R.string.fragment_weather_few_clouds,
            802 to R.string.fragment_weather_scattered_clouds,
            803 to R.string.fragment_weather_broken_clouds,
            804 to R.string.fragment_weather_overcast_clouds)

    private val weatherConditionIconMap: Map<String, Int> = hashMapOf(
            "01d" to R.drawable.ic_sun,
            "02d" to R.drawable.ic_few_clouds_sun,
            "02d" to R.drawable.ic_few_clouds_sun,
            "03d" to R.drawable.ic_cloudy,
            "04d" to R.drawable.ic_cloudy,
            "09d" to R.drawable.ic_rain,
            "10d" to R.drawable.ic_rain,
            "11d" to R.drawable.ic_thundestorm,
            "12d" to R.drawable.ic_snow,
            "50d" to R.drawable.ic_mist,
            "01n" to R.drawable.ic_moon,
            "02n" to R.drawable.ic_few_clouds_moon,
            "02n" to R.drawable.ic_few_clouds_moon,
            "03n" to R.drawable.ic_cloudy,
            "04n" to R.drawable.ic_cloudy,
            "09n" to R.drawable.ic_rain,
            "10n" to R.drawable.ic_rain,
            "11n" to R.drawable.ic_thundestorm,
            "12n" to R.drawable.ic_snow,
            "50n" to R.drawable.ic_mist)

    fun convertWeatherConditionToDescriptionStringId(weatherCondition: WeatherCondition) : Int {
        var weatherConditionStringId : Int = weatherConditionDescriptionMap[weatherCondition.id] ?: 0

        if (weatherConditionStringId == 0) {
            val weatherConditionId = weatherCondition.id / 100 * 100
            weatherConditionStringId = weatherConditionDescriptionMap[weatherConditionId] ?: 0
        }

        return if (weatherConditionStringId != 0) {
            weatherConditionStringId
        } else {
            R.string.fragment_weather_unknown
        }
    }

    fun convertWeatherConditionToIconId(weatherCondition: WeatherCondition) : Int {
        val weatherConditionIconId : Int = weatherConditionIconMap[weatherCondition.iconId] ?: 0

        return if (weatherConditionIconId != 0) {
            weatherConditionIconId
        } else {
            R.drawable.ic_mist
        }
    }
}