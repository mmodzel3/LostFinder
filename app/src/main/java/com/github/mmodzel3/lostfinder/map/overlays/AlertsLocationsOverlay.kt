package com.github.mmodzel3.lostfinder.map.overlays

import android.content.Context
import com.github.mmodzel3.lostfinder.alert.Alert
import com.github.mmodzel3.lostfinder.alert.AlertTypeTitleConverter
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class AlertsLocationsOverlay(private val map: MapView, private val context: Context): DataLocationsOverlay<Alert>(map) {
    override fun createMarker(data: Alert): Marker {
        val marker = Marker(map)
        setMarkerTitle(marker, data)

        return marker
    }

    private fun setMarkerTitle(marker: Marker, alert: Alert) {
        marker.title = AlertTypeTitleConverter.convertAlertTypeToTitle(context, alert.type) +
                " [" + alert.user.username + "]"
    }
}