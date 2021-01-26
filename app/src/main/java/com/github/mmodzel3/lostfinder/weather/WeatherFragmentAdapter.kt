package com.github.mmodzel3.lostfinder.weather

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class WeatherFragmentAdapter(fragmentActivity: FragmentActivity)
    : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int = 4

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            WeatherFragment.create(WeatherFragment.WEATHER_NOW_TYPE)
        } else if (position == 1) {
            WeatherFragment.create(WeatherFragment.WEATHER_NEXT_HOUR_TYPE)
        } else if (position == 2) {
            WeatherFragment.create(WeatherFragment.WEATHER_TODAY_TYPE)
        } else {
            WeatherFragment.create(WeatherFragment.WEATHER_TOMORROW_TYPE)
        }
    }
}