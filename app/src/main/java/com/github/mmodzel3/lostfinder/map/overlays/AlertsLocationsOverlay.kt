package com.github.mmodzel3.lostfinder.map.overlays

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.alert.Alert
import com.github.mmodzel3.lostfinder.alert.AlertType
import com.github.mmodzel3.lostfinder.alert.AlertTypeTitleConverter
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class AlertsLocationsOverlay(private val map: MapView, private val context: Context): DataLocationsOverlay<Alert>(map) {

    override fun createMarker(data: Alert): Marker {
        val marker = Marker(map)
        setMarkerTitle(marker, data)
        setMarkerDescription(marker, data)
        setMarkerIcon(marker, data)

        return marker
    }

    private fun setMarkerTitle(marker: Marker, alert: Alert) {
        marker.title = AlertTypeTitleConverter.convertAlertTypeToTitle(context, alert.type) +
                " [" + alert.user.username + "]"
    }

    private fun setMarkerDescription(marker: Marker, alert: Alert) {
        marker.subDescription = alert.description
    }

    private fun setMarkerIcon(marker: Marker, alert: Alert) {
        when(alert.type) {
            AlertType.SEARCH -> {
                marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_location_marker_alert_search)
            }
            AlertType.GATHER -> {
                marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_location_marker_alert_gather)
            }
            else -> {
                marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_location_marker_alert)
            }
        }
    }
}