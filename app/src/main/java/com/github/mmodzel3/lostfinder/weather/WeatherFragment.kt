package com.github.mmodzel3.lostfinder.weather

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.mmodzel3.lostfinder.R

class WeatherFragment: Fragment() {
    companion object {
        const val WEATHER_NOW_TYPE = 0
        const val WEATHER_NEXT_HOUR_TYPE = 1
        const val WEATHER_TODAY_TYPE = 2
        const val WEATHER_TOMORROW_TYPE = 3

        private const val TYPE = "type"

        fun create(type: Int): WeatherFragment {
            val weatherFragment = WeatherFragment()
            val bundle = Bundle(1)

            bundle.putInt(TYPE, type)

            weatherFragment.arguments = bundle
            return weatherFragment
        }
    }

    internal lateinit var weatherViewModel: WeatherViewModel

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

        weatherViewModel = provideWeatherViewModel()

        showWeatherType()
        observeWeatherData()
    }

    internal fun observeWeatherData() {
        val observer = Observer<Weather> {
            showWeatherData(it)
        }

        when (arguments?.getInt(TYPE)) {
            WEATHER_NOW_TYPE -> {
                weatherViewModel.now.observe(viewLifecycleOwner, observer)
            }
            WEATHER_NEXT_HOUR_TYPE -> {
                weatherViewModel.nextHour.observe(viewLifecycleOwner, observer)
            }
            WEATHER_TODAY_TYPE -> {
                weatherViewModel.today.observe(viewLifecycleOwner, observer)
            }
            else -> {
                weatherViewModel.tomorrow.observe(viewLifecycleOwner, observer)
            }
        }
    }

    private fun provideWeatherViewModel(): WeatherViewModel {
        val weatherApiKey: String = requireView().context.getString(R.string.weather_api_key)
        val weatherUnits: String = requireView().context.getString(R.string.activity_weather_units)
        val viewModelFactory = WeatherViewModelFactory(weatherApiKey, weatherUnits)

        return ViewModelProvider(requireActivity(), viewModelFactory)
                .get(WeatherViewModel::class.java)
    }

    private fun showWeatherType() {
        when (arguments?.getInt(TYPE)) {
            WEATHER_NOW_TYPE -> {
                weatherTypeNameTextView.text = view?.context?.getString(R.string.fragment_weather_now_full)
            }
            WEATHER_NEXT_HOUR_TYPE -> {
                weatherTypeNameTextView.text = view?.context?.getString(R.string.fragment_weather_next_hour_full)
            }
            WEATHER_TODAY_TYPE -> {
                weatherTypeNameTextView.text = view?.context?.getString(R.string.fragment_weather_today_full)
            }
            else -> {
                weatherTypeNameTextView.text = view?.context?.getString(R.string.fragment_weather_tomorrow_full)
            }
        }
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