package com.github.mmodzel3.lostfinder.map

import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.github.mmodzel3.lostfinder.map.overlays.UsersLocationsOverlay
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.user.*
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker

open class UsersLocationMapActivity : CurrentLocationMapWithCenteringActivity() {

    private lateinit var usersLocationsOverlay: UsersLocationsOverlay
    private val userEndpoint: UserEndpoint by lazy {
        UserEndpointFactory.createUserEndpoint(TokenManager.getInstance(applicationContext))
    }

    private val userEndpointViewModel: UserEndpointViewModel by viewModels {
        UserEndpointViewModelFactory(userEndpoint)
    }

    override fun initMap() {
        super.initMap()

        usersLocationsOverlay = UsersLocationsOverlay(map)
        userEndpointViewModel.users.observe(this, Observer {
            usersLocationsOverlay.updateUsersLocations(it)
            map.invalidate()
        })
        map.overlays.add(usersLocationsOverlay)
    }
}