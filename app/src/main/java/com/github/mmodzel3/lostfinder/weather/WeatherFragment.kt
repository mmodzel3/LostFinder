package com.github.mmodzel3.lostfinder.weather

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.github.mmodzel3.lostfinder.R

class WeatherFragment(private val type: Int,
                      private val weather: MutableLiveData<Weather>): Fragment() {
    companion object {
        const val WEATHER_NOW_TYPE = 0
        const val WEATHER_NEXT_HOUR_TYPE = 1
        const val WEATHER_TODAY_TYPE = 2
        const val WEATHER_TOMORROW_TYPE = 3
    }
    private lateinit var weatherTypeNameTextView: TextView
    private lateinit var weatherConditionTextView: TextView
    private lateinit var temperatureTextView: TextView
    private lateinit var pressureTextView: TextView
    private lateinit var humidityTextView: TextView
    private lateinit var cloudsTextView: TextView
    private lateinit var visibilityTextView: TextView
    private lateinit var windSpeedTextView: TextView
    private lateinit var windDegreeTextView: TextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        weatherTypeNameTextView = view.findViewById(R.id.fragment_weather_tv_type_name)
        weatherConditionTextView = view.findViewById(R.id.fragment_weather_tv_weather_condition)
        temperatureTextView = view.findViewById(R.id.fragment_weather_tv_temperature)
        pressureTextView = view.findViewById(R.id.fragment_weather_tv_pressure)
        humidityTextView = view.findViewById(R.id.fragment_weather_tv_humidity)
        cloudsTextView = view.findViewById(R.id.fragment_weather_tv_clouds)
        visibilityTextView = view.findViewById(R.id.fragment_weather_tv_visibility)
        windSpeedTextView = view.findViewById(R.id.fragment_weather_tv_wind_speed)
        windDegreeTextView = view.findViewById(R.id.fragment_weather_tv_wind_degree)

        showWeatherType()
        observeWeatherData()
    }

    private fun showWeatherType() {
        if (type == WEATHER_NOW_TYPE) {
            weatherTypeNameTextView.text = view?.context?.getString(R.string.fragment_weather_now_full)
        } else if (type == WEATHER_NEXT_HOUR_TYPE) {
            weatherTypeNameTextView.text = view?.context?.getString(R.string.fragment_weather_next_hour_full)
        } else if (type == WEATHER_TODAY_TYPE) {
            weatherTypeNameTextView.text = view?.context?.getString(R.string.fragment_weather_today_full)
        } else {
            weatherTypeNameTextView.text = view?.context?.getString(R.string.fragment_weather_tomorrow_full)
        }
    }

    private fun observeWeatherData() {
        weather.observe(viewLifecycleOwner, Observer {
            showWeatherData(it)
        })
    }

    private fun showWeatherData(weather: Weather) {
        showWeatherCondition(weather.weatherConditionList[0])
        showTemperature(weather.temperature)
        showPressure(weather.pressure)
        showClouds(weather.clouds)
        showHumidity(weather.humidity)
        showVisibility(weather.visibility)
        showWindSpeed(weather.windSpeed)
        showWindDegree(weather.windDegree)
    }

    private fun showWeatherCondition(weatherCondition: WeatherCondition) {
        val v: View? = view

        if (v != null) {
            val conditionIconId: Int = WeatherConditionConverter
                    .convertWeatherConditionToIconId(weatherCondition)
            val conditionDescriptionStringId: Int = WeatherConditionConverter
                    .convertWeatherConditionToDescriptionStringId(weatherCondition)
            val conditionDescriptionString: String = v.context.getString(conditionDescriptionStringId)

            weatherConditionTextView.setCompoundDrawablesWithIntrinsicBounds(conditionIconId, 0, 0, 0)
            weatherConditionTextView.text = conditionDescriptionString
        }
    }

    private fun showTemperature(temperature: Double) {
        temperatureTextView.text = temperature.toString()
    }

    private fun showPressure(pressure: Int) {
        pressureTextView.text = pressure.toString()
    }

    private fun showHumidity(humidity: Int) {
        humidityTextView.text = humidity.toString()
    }

    private fun showClouds(clouds: Int) {
        cloudsTextView.text = clouds.toString()
    }

    private fun showVisibility(visibility: Int) {
        visibilityTextView.text = visibility.toString()
    }

    private fun showWindSpeed(windSpeed: Double) {
        windSpeedTextView.text = windSpeed.toString()
    }

    private fun showWindDegree(windDegree: Double) {
        windDegreeTextView.text = windDegree.toString()
    }
}