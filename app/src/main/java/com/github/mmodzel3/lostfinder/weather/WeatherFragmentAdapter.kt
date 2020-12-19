package com.github.mmodzel3.lostfinder.weather

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class WeatherFragmentAdapter(fragmentActivity: FragmentActivity,
                             private val weatherEndpointViewModel: WeatherEndpointViewModel)
    : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            WeatherFragment(WeatherFragment.WEATHER_NOW_TYPE, weatherEndpointViewModel.now)
        } else if (position == 1) {
            WeatherFragment(WeatherFragment.WEATHER_NEXT_HOUR_TYPE, weatherEndpointViewModel.nextHour)
        } else if (position == 2) {
            WeatherFragment(WeatherFragment.WEATHER_TODAY_TYPE, weatherEndpointViewModel.today)
        } else {
            WeatherFragment(WeatherFragment.WEATHER_TOMORROW_TYPE, weatherEndpointViewModel.tomorrow)
        }
    }
}