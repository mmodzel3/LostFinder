package com.github.mmodzel3.lostfinder.weather

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.widget.ViewPager2
import com.github.mmodzel3.lostfinder.LoggedUserActivityAbstract
import com.github.mmodzel3.lostfinder.MainActivity
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.alert.AlertActivity
import com.github.mmodzel3.lostfinder.chat.ChatActivity
import com.github.mmodzel3.lostfinder.location.CurrentLocationBinder
import com.github.mmodzel3.lostfinder.location.CurrentLocationListener
import com.github.mmodzel3.lostfinder.location.CurrentLocationService
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class WeatherActivity: LoggedUserActivityAbstract() {
    private lateinit var viewPager: ViewPager2

    private lateinit var currentLocationBinder : CurrentLocationBinder
    private lateinit var currentLocationConnection : ServiceConnection
    private var currentLocationListener: CurrentLocationListener? = null

    private var fetchedWeatherData: Boolean = false

    private val weatherEndpoint: WeatherEndpoint by lazy {
        WeatherEndpointFactory.createWeatherEndpoint()
    }

    private val weatherEndpointViewModel: WeatherEndpointViewModel by viewModels {
        val weatherApiKey: String = applicationContext.getString(R.string.weather_api_key)
        val weatherUnits: String = applicationContext.getString(R.string.activity_weather_units)
        WeatherEndpointViewModelFactory(weatherEndpoint, weatherApiKey, weatherUnits)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_weather)

        initViewPager()
        initTabLayout()
        bindToCurrentLocationService()
        observeWeatherStatus()
    }

    override fun onDestroy() {
        super.onDestroy()

        stopListeningToCurrentLocation()
    }

    internal fun onLocationChange(latitude: Double, longitude: Double) {
        if (!fetchedWeatherData) {
            fetchedWeatherData = true
            weatherEndpointViewModel.forceFetchData(latitude, longitude)
        }
    }

    private fun initViewPager() {
        viewPager = findViewById(R.id.activity_weather_pager)
        viewPager.adapter = WeatherFragmentAdapter(this, weatherEndpointViewModel)
    }

    private fun initTabLayout() {
        val tabLayout: TabLayout = findViewById(R.id.activity_weather_tab_layout)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (position) {
                WeatherFragment.WEATHER_NOW_TYPE -> {
                    tab.text = applicationContext.getString(R.string.fragment_weather_now)
                }
                WeatherFragment.WEATHER_NEXT_HOUR_TYPE -> {
                    tab.text = applicationContext.getString(R.string.fragment_weather_next_hour)
                }
                WeatherFragment.WEATHER_TODAY_TYPE -> {
                    tab.text = applicationContext.getString(R.string.fragment_weather_today)
                }
                WeatherFragment.WEATHER_TOMORROW_TYPE -> {
                    tab.text = applicationContext.getString(R.string.fragment_weather_tomorrow)
                }
            }
        }.attach()
    }

    private fun bindToCurrentLocationService() {
        currentLocationConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                currentLocationBinder = service as CurrentLocationBinder
                listenToCurrentLocation()
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        }

        Intent(this, CurrentLocationService::class.java).also { intent ->
            bindService(intent, currentLocationConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun listenToCurrentLocation() {
        currentLocationListener = object: CurrentLocationListener {
            override fun onLocalisationChange(location: Location) {
                onLocationChange(location.latitude, location.longitude)
            }
        }

        currentLocationBinder.registerListener(currentLocationListener!!)
    }

    private fun stopListeningToCurrentLocation() {
        if (currentLocationListener != null) {
            currentLocationBinder.unregisterListener(currentLocationListener!!)
        }
    }

    private fun observeWeatherStatus() {
        val activity: Activity = this
        weatherEndpointViewModel.weatherStatus.observe(this, Observer {
            when(it!!) {
                WeatherEndpointStatus.OK -> {}
                WeatherEndpointStatus.FETCHING -> Toast.makeText(activity, R.string.activity_weather_msg_fetching,
                        Toast.LENGTH_SHORT).show()
                WeatherEndpointStatus.ERROR -> {
                    fetchedWeatherData = false
                    Toast.makeText(activity, R.string.activity_weather_err_api_access_error,
                            Toast.LENGTH_LONG).show()
                }
            }
        })
    }


}