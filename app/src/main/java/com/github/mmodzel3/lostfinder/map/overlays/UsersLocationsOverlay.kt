package com.github.mmodzel3.lostfinder.map.overlays

import android.content.Context
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.user.User
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class UsersLocationsOverlay(private val map: MapView, private val context: Context): DataLocationsOverlay<User>(map) {
    private val roleUser = context.getString(R.string.role_user)
    private val roleAdmin = context.getString(R.string.role_admin)
    private val roleOwner = context.getString(R.string.role_owner)

    override fun createMarker(data: User): Marker {
        val marker = Marker(map)
        setMarkerTitle(marker, data)

        return marker
    }

    private fun setMarkerTitle(marker: Marker, user: User) {
        val role: String = when {
            user.isUser() -> {
                roleUser
            }
            user.isAdmin() -> {
                roleAdmin
            }
            else -> {
                roleOwner
            }
        }

        marker.title = user.username + "\n[" + role + "]"
    }
}