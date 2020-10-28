package com.github.mmodzel3.lostfinder.map

import android.widget.ImageButton
import com.github.mmodzel3.lostfinder.R


open class CurrentLocationMapWithCenteringActivity : CurrentLocationMapActivity() {
    override fun initMap() {
        super.initMap()

        initMapCentering()
    }

    private fun initMapCentering() {
        val centerMapToUserButton : ImageButton = findViewById(R.id.center_map_current_location)
        centerMapToUserButton.setOnClickListener {
            mapController.animateTo(currentLocationMarker.position)
        }
    }
}