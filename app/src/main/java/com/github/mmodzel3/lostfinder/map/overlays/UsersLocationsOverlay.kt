package com.github.mmodzel3.lostfinder.map.overlays

import android.content.Context
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.user.User
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker


class UsersLocationsOverlay(private val map: MapView, private val context: Context): FolderOverlay() {
    internal val usersMarkers: MutableMap<String, Marker> = mutableMapOf()
    private val role_user = context.getString(R.string.role_user)
    private val role_admin = context.getString(R.string.role_admin)
    private val role_owner = context.getString(R.string.role_owner)
    private val lock: Any = Any()

    fun updateUsersLocations(users: Map<String, User>) = synchronized(lock) {
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
        if (user.location != null) {
            val marker: Marker = createMarker(user)
            marker.position = GeoPoint(user.location.latitude, user.location.longitude)

            usersMarkers[user.id] = marker
            add(marker)
        }
    }

    private fun createMarker(user: User): Marker {
        val marker = Marker(map)
        setMarkerTitle(marker, user)

        return marker
    }

    private fun setMarkerTitle(marker: Marker, user: User) {
        var role: String = when {
            user.isUser() -> {
                role_user
            }
            user.isAdmin() -> {
                role_admin
            }
            else -> {
                role_owner
            }
        }

        marker.title = user.username + "\n[" + role + "]"
    }

    private fun updateMarker(user: User) {
        if (user.location != null) {
            val marker: Marker = usersMarkers[user.id]!!
            marker.position = GeoPoint(user.location.latitude, user.location.longitude)
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

    private fun removeOldData(users: Map<String, User>) {
        val keysToRemove: MutableList<String> = ArrayList()

        usersMarkers.forEach {
            if(it.key !in users.keys) {
                keysToRemove.add(it.key)
            }
        }

        keysToRemove.forEach {
            usersMarkers.remove(it)
        }
    }
}