package com.github.mmodzel3.lostfinder

import android.os.Bundle
import com.github.mmodzel3.lostfinder.map.DataLocationsMapActivity

class MainActivity: DataLocationsMapActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_map)
        initMap()
    }

    override fun onDestroy() {
        super.onDestroy()
        deInitMap()
    }
}
