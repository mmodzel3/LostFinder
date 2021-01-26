package com.github.mmodzel3.lostfinder.map

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.location.Location
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker


class ChooseLocationMapActivity : DataLocationsWithNavDrawerMapActivity() {
    companion object {
        const val LOCATION_LATITUDE_INTENT = "LOCATION_LATITUDE_INTENT"
        const val LOCATION_LONGITUDE_INTENT = "LOCATION_LONGITUDE_INTENT"
    }

    private lateinit var chosenLocation: Location
    private lateinit var chosenLocationMarker: Marker
    private var isLocationChosen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_choose_location_map)
        initMap()
    }

    override fun initMap() {
        super.initMap()

        initMapSetLocation()
        initConfirmButton()
        initCancelButton()
    }

    override fun onDestroy() {
        super.onDestroy()
        deInitMap()
    }

    private fun initMapSetLocation() {
        val mapEventsReceiver: MapEventsReceiver = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(point: GeoPoint): Boolean {
                setChosenLocation(point)
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean {
                return false
            }
        }

        map.overlays.add(MapEventsOverlay(mapEventsReceiver))
    }

    private fun initConfirmButton() {
        val confirmButton: ImageButton = findViewById(R.id.activity_choose_location_map_confirm)

        confirmButton.isEnabled = false
        confirmButton.setOnClickListener {
            finishSendLocationBack()
        }
    }

    private fun initCancelButton() {
        val cancelButton: ImageButton = findViewById(R.id.activity_choose_location_map_cancel)

        cancelButton.setOnClickListener {
            finishCancel()
        }
    }

    private fun setChosenLocation(point: GeoPoint) {
        chosenLocation = Location(point.latitude, point.longitude)

        if (!isLocationChosen) {
            addChosenLocationMarkerToMap(point)
            isLocationChosen = true
            enableConfirmButton()
        } else {
            chosenLocationMarker.position = point
        }

        map.invalidate()
    }

    private fun addChosenLocationMarkerToMap(point: GeoPoint) {
        chosenLocationMarker = Marker(map)
        chosenLocationMarker.position = point
        chosenLocationMarker.icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_location_marker_add)
        chosenLocationMarker.title = getString(R.string.activity_map_chosen_location_title)
        map.overlays.add(chosenLocationMarker)
    }

    private fun enableConfirmButton() {
        val confirmButton: ImageButton = findViewById(R.id.activity_choose_location_map_confirm)
        confirmButton.isEnabled = true
    }

    private fun finishSendLocationBack() {
        val output = Intent()
        output.putExtra(LOCATION_LATITUDE_INTENT, chosenLocation.latitude)
        output.putExtra(LOCATION_LONGITUDE_INTENT, chosenLocation.longitude)
        setResult(RESULT_OK, output)
        finish()
    }

    private fun finishCancel() {
        setResult(RESULT_CANCELED)
        finish()
    }
}