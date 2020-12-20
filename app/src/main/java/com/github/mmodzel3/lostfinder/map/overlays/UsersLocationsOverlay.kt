package com.github.mmodzel3.lostfinder.map.overlays

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.user.User
import com.github.mmodzel3.lostfinder.user.UserRole
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker


class UsersLocationsOverlay(private val map: MapView, private val context: Context): DataLocationsOverlay<User>(map) {
    private val roleUser = context.getString(R.string.role_user)
    private val roleAdmin = context.getString(R.string.role_admin)
    private val roleOwner = context.getString(R.string.role_owner)

    override fun createMarker(data: User): Marker {
        val marker = Marker(map)
        setMarkerTitle(marker, data)
        setMarkerColor(marker, data)

        return marker
    }

    private fun setMarkerTitle(marker: Marker, user: User) {
        val role: String = when(user.role) {
            UserRole.MANAGER -> {
                roleAdmin
            }
            UserRole.OWNER -> {
                roleOwner
            }
            else -> {
                roleUser
            }
        }

        marker.title = user.username + "\n[" + role + "]"
    }

    private fun setMarkerColor(marker: Marker, user: User) {
        when(user.role) {
            UserRole.MANAGER -> {
                marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_location_marker_manager)
            }
            UserRole.OWNER -> {
                marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_location_marker_owner)
            }
            else -> {
                marker.icon = ContextCompat.getDrawable(context, R.drawable.ic_location_marker_user)
            }
        }
    }
}