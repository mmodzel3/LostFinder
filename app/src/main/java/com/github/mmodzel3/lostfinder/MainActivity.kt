package com.github.mmodzel3.lostfinder

import android.os.Bundle
import android.view.View
import com.github.mmodzel3.lostfinder.map.CurrentLocationMapWithCenteringActivity


class MainActivity : CurrentLocationMapWithCenteringActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        initMap()
    }

    override fun onDestroy() {
        super.onDestroy()
        deInitMap()
    }
}