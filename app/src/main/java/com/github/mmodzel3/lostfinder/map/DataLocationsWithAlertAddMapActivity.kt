package com.github.mmodzel3.lostfinder.map

import android.content.Intent
import android.widget.ImageButton
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.alert.AlertAddActivity

open class DataLocationsWithAlertAddMapActivity : DataLocationsWithNavDrawerMapActivity() {

    override fun initMap() {
        super.initMap()

        initAddAlertButton()
    }

    private fun initAddAlertButton() {
        val addAlertButton: ImageButton = findViewById(R.id.activity_map_add_alert)

        addAlertButton.setOnClickListener {
            onAddAlertButton()
        }
    }

    private fun onAddAlertButton() {
        goToAddAlertActivity()
    }

    private fun goToAddAlertActivity() {
        val intent = Intent(this, AlertAddActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
    }
}