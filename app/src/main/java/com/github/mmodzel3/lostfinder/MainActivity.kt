package com.github.mmodzel3.lostfinder

import android.os.Bundle
import com.github.mmodzel3.lostfinder.map.DataLocationsWithAlertAddMapActivity

class MainActivity: DataLocationsWithAlertAddMapActivity() {
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
