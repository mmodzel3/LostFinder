package com.github.mmodzel3.lostfinder.map

import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.alert.*
import com.github.mmodzel3.lostfinder.map.overlays.AlertsLocationsOverlay
import com.github.mmodzel3.lostfinder.map.overlays.UsersLocationsOverlay
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus
import com.github.mmodzel3.lostfinder.user.*

open class DataLocationsMapActivity : CurrentLocationMapWithCenteringActivity() {

    private lateinit var usersLocationsOverlay: UsersLocationsOverlay
    private lateinit var alertsLocationsOverlay: AlertsLocationsOverlay

    private val tokenManager: TokenManager by lazy {
        TokenManager.getInstance(applicationContext)
    }

    protected val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(tokenManager)
    }

    protected val alertViewModel: AlertViewModel by viewModels {
        AlertViewModelFactory(tokenManager)
    }

    override fun initMap() {
        super.initMap()

        initUsersLocationsOverlay()
        initAlertsLocationsOverlay()
    }

    override fun onResume() {
        super.onResume()

        userViewModel.runUpdates()
        alertViewModel.runUpdates()
    }

    override fun onPause() {
        super.onPause()

        userViewModel.stopUpdates()
        alertViewModel.stopUpdates()
    }

    private fun initUsersLocationsOverlay() {
        usersLocationsOverlay = UsersLocationsOverlay(map, applicationContext)
        userViewModel.users.observe(this, Observer {
            val users: Map<String, User> = it.filter {
                it.value.email != tokenManager.getTokenEmailAddress()
            }

            usersLocationsOverlay.updateDataLocations(users)
            map.invalidate()
        })

        userViewModel.status.observe(this, Observer {
            when(it) {
                ServerEndpointStatus.FETCHING -> {
                    Toast.makeText(this, R.string.msg_fetching, Toast.LENGTH_SHORT).show()
                }

                ServerEndpointStatus.INVALID_TOKEN -> {
                    Toast.makeText(this, R.string.err_invalid_token, Toast.LENGTH_LONG).show()
                    goToLoginActivity()
                }

                ServerEndpointStatus.ERROR -> Toast.makeText(this, R.string.err_fetching, Toast.LENGTH_LONG).show()
                else -> { }
            }
        })

        map.overlays.add(usersLocationsOverlay)
    }

    private fun initAlertsLocationsOverlay() {
        alertsLocationsOverlay = AlertsLocationsOverlay(map, applicationContext)
        alertViewModel.alerts.observe(this, Observer {

            alertsLocationsOverlay.updateDataLocations(it)
            map.invalidate()
        })

        userViewModel.status.observe(this, Observer {
            when(it) {
                ServerEndpointStatus.INVALID_TOKEN -> {
                    Toast.makeText(this, R.string.err_invalid_token, Toast.LENGTH_LONG).show()
                    goToLoginActivity()
                }

                ServerEndpointStatus.ERROR -> Toast.makeText(this, R.string.err_fetching, Toast.LENGTH_LONG).show()
                else -> { }
            }
        })

        map.overlays.add(alertsLocationsOverlay)
    }
}