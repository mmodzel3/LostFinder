package com.github.mmodzel3.lostfinder.weather

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.github.mmodzel3.lostfinder.LoggedUserActivityAbstract
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.location.CurrentLocationBinder
import com.github.mmodzel3.lostfinder.location.CurrentLocationListener
import com.github.mmodzel3.lostfinder.location.CurrentLocationService
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class WeatherActivity: LoggedUserActivityAbstract() {
    companion object {
        const val CHECK_LOCATION_CHANGE_DELAY = 1500L
    }

    private lateinit var viewPager: ViewPager2

    private lateinit var currentLocationBinder : CurrentLocationBinder
    private lateinit var currentLocationConnection : ServiceConnection
    private var currentLocationListener: CurrentLocationListener? = null
    private var lastLocation: Location? = null

    private var fetchedFirstWeatherData: Boolean = false

    private lateinit var handler: Handler
    private var checkLocationPresenceRunnable: Runnable? = null

    private val weatherViewModel: WeatherViewModel by viewModels {
        val weatherApiKey: String = applicationContext.getString(R.string.weather_api_key)
        val weatherUnits: String = applicationContext.getString(R.string.activity_weather_units)
        WeatherViewModelFactory(weatherApiKey, weatherUnits)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_weather)

        initViewPager()
        initTabLayout()
        bindToCurrentLocationService()
        observeWeatherStatus()
    }

    override fun onResume() {
        super.onResume()

        if (lastLocation != null) {
            weatherViewModel.updateWeatherLocation(lastLocation!!.latitude, lastLocation!!.longitude)
            weatherViewModel.runPeriodicFetchData()
        } else {
            runDelayedLocationPresenceCheck()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindFromCurrentLocationService()
        stopDelayedLocationPresenceCheck()
        weatherViewModel.stopPeriodicFetchData()
    }

    internal fun onLocationChange(latitude: Double, longitude: Double) {
        weatherViewModel.updateWeatherLocation(latitude, longitude)

        if (!fetchedFirstWeatherData) {
            weatherViewModel.runPeriodicFetchData()
            fetchedFirstWeatherData = true
        }
    }

    private fun initViewPager() {
        viewPager = findViewById(R.id.activity_weather_pager)
        viewPager.adapter = WeatherFragmentAdapter(this)
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
                lastLocation = location
                onLocationChange(location.latitude, location.longitude)
            }
        }

        currentLocationBinder.registerListener(currentLocationListener!!)
    }

    private fun unbindFromCurrentLocationService() {
        stopListeningToCurrentLocation()
        unbindService(currentLocationConnection)
    }

    private fun stopListeningToCurrentLocation() {
        if (currentLocationListener != null) {
            currentLocationBinder.unregisterListener(currentLocationListener!!)
        }
    }

    private fun observeWeatherStatus() {
        val activity: Activity = this
        weatherViewModel.weatherStatus.observe(this, {
            when(it!!) {
                WeatherEndpointStatus.OK -> {}
                WeatherEndpointStatus.FETCHING -> Toast.makeText(activity, R.string.activity_weather_msg_fetching,
                        Toast.LENGTH_SHORT).show()
                WeatherEndpointStatus.ERROR -> {
                    Toast.makeText(activity, R.string.activity_weather_err_api_access_error,
                            Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun runDelayedLocationPresenceCheck() {
        handler = Handler(Looper.getMainLooper())
        checkLocationPresenceRunnable = Runnable {
            if (lastLocation == null) {
                Toast.makeText(this@WeatherActivity, R.string.activity_weather_msg_waiting,
                    Toast.LENGTH_LONG).show()
            }
        }

        handler.postDelayed(checkLocationPresenceRunnable!!, CHECK_LOCATION_CHANGE_DELAY)
    }

    private fun stopDelayedLocationPresenceCheck() {
        if (checkLocationPresenceRunnable != null) {
            handler.removeCallbacks(checkLocationPresenceRunnable!!)
        }
    }
}