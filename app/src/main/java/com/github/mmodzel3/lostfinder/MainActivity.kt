package com.github.mmodzel3.lostfinder

import android.content.Intent
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

    override fun onBackPressed() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }
}
