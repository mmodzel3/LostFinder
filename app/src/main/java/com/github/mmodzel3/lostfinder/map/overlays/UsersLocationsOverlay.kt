package com.github.mmodzel3.lostfinder.map.overlays

import com.github.mmodzel3.lostfinder.user.User
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker

class UsersLocationsOverlay(private val map: MapView): FolderOverlay() {
    private var usersMarkers: MutableMap<String, Marker> = mutableMapOf()

    private val lock: Any = Any()

    fun updateUsersLocations(users: Collection<User>) = synchronized(lock) {
        users.forEach {
            var marker: Marker? = usersMarkers[it.id]

            if (marker == null) {
                marker = addMarker(it)
            } else {
                updateMarker(marker, it)
            }

            usersMarkers[it.id] = marker
        }
    }

    private fun addMarker(user: User): Marker {
        val marker = Marker(map)
        marker.position = GeoPoint(user.latitude, user.longitude)
        add(marker)

        return marker
    }

    private fun updateMarker(marker: Marker, user: User): Marker {
        marker.position = GeoPoint(user.latitude, user.longitude)
        return marker
    }
}