package com.github.mmodzel3.lostfinder.map.overlays

import com.github.mmodzel3.lostfinder.user.User
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker

class UsersLocationsOverlay(private val map: MapView): FolderOverlay() {
    private val usersMarkers: MutableMap<String, Marker> = mutableMapOf()

    private val lock: Any = Any()

    fun updateUsersLocations(users: MutableMap<String, User>) = synchronized(lock) {
        users.forEach {
            updateUserLocation(it.value)
        }

        if (usersMarkers.size != users.size) {
            removeOldData(users)
        }
    }

    private fun updateUserLocation(user: User) {
        if (usersMarkers[user.id] == null) {
            addMarker(user)
        } else {
            updateMarker(user)
        }
    }

    private fun addMarker(user: User) {
        if (user.latitude != null && user.longitude != null) {
            val marker = Marker(map)
            marker.position = GeoPoint(user.latitude, user.longitude)

            usersMarkers[user.id] = marker
            add(marker)
        }
    }

    private fun updateMarker(user: User) {
        if (user.latitude != null && user.longitude != null) {
            val marker: Marker = usersMarkers[user.id]!!
            marker.position = GeoPoint(user.latitude, user.longitude)
            usersMarkers[user.id] = marker
        } else {
            removeMarker(user)
        }
    }

    private fun removeMarker(user: User) {
        removeMarker(user.id)
    }

    private fun removeMarker(userId: String) {
        val marker: Marker = usersMarkers[userId]!!
        usersMarkers.remove(userId)
        remove(marker)
    }

    private fun removeOldData(users: MutableMap<String, User>) {
        usersMarkers.forEach {
            if(it.key !in users.keys) {
                removeMarker(it.key)
            }
        }
    }
}