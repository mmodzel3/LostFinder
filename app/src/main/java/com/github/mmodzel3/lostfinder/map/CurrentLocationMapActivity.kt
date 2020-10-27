package com.github.mmodzel3.lostfinder.map

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.location.CurrentLocationBinder
import com.github.mmodzel3.lostfinder.location.CurrentLocationListener
import com.github.mmodzel3.lostfinder.location.CurrentLocationService
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker


open class CurrentLocationMapActivity : BaseMapActivity() {
    protected lateinit var currentLocationBinder : CurrentLocationBinder
    protected lateinit var currentLocationMarker: Marker

    private lateinit var currentLocationConnection : ServiceConnection

    override fun initMap() {
        super.initMap()
        bindToCurrentLocationService()
    }

    override fun deInitMap() {
        super.deInitMap()
        unbindFromCurrentLocationService()
    }

    private fun bindToCurrentLocationService() {
        currentLocationConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                currentLocationBinder = service as CurrentLocationBinder
                initCurrentLocationMarker()
            }

            override fun onServiceDisconnected(name: ComponentName?) {

            }
        }

        Intent(this, CurrentLocationService::class.java).also { intent ->
            bindService(intent, currentLocationConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun initCurrentLocationMarker() {
        currentLocationMarker = Marker(map)
        currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        map.overlays.add(currentLocationMarker)

        listenToCurrentLocation()
    }

    private fun listenToCurrentLocation() {
        currentLocationBinder.registerListener(object : CurrentLocationListener {
            override fun onLocalisationChange(location: Location) {
                val point = GeoPoint(location)
                currentLocationMarker.position = point
                mapController.setCenter(point)
            }
        })
    }

    private fun unbindFromCurrentLocationService() {
        unbindService(currentLocationConnection)
    }
}