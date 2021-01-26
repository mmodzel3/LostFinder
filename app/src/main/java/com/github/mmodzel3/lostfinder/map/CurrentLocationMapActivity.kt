package com.github.mmodzel3.lostfinder.map

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.location.Location
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.location.CurrentLocationBinder
import com.github.mmodzel3.lostfinder.location.CurrentLocationListener
import com.github.mmodzel3.lostfinder.location.CurrentLocationService
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker


open class CurrentLocationMapActivity : BaseMapActivity() {
    protected lateinit var currentLocationBinder : CurrentLocationBinder
    protected lateinit var currentLocationMarker: Marker
    protected var currentLocationListener: CurrentLocationListener? = null

    private lateinit var currentLocationConnection : ServiceConnection
    private var gotFirstLocation: Boolean = false

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            currentLocationMarker.icon = ContextCompat.getDrawable(applicationContext, R.drawable.ic_location_marker_current)
        }

        currentLocationMarker.title = getString(R.string.activity_map_current_location_marker_title)

        map.overlays.add(currentLocationMarker)

        listenToCurrentLocation()
    }

    private fun listenToCurrentLocation() {
        currentLocationListener = object : CurrentLocationListener {
            override fun onLocalisationChange(location: Location) {
                currentLocationMarker.position = GeoPoint(location)

                if (!gotFirstLocation) {
                    gotFirstLocation = true
                    mapController.setCenter(currentLocationMarker.position)
                }
            }
        }

        currentLocationBinder.registerListener(currentLocationListener!!)
    }

    private fun unbindFromCurrentLocationService() {
        stopListeningToCurrentLocation()
        unbindService(currentLocationConnection)
    }

    private fun stopListeningToCurrentLocation() {
        if (currentLocationListener != null) {
            currentLocationBinder.unregisterListener(currentLocationListener!!)
        }
    }
}