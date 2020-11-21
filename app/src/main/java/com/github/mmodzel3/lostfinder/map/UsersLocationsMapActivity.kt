package com.github.mmodzel3.lostfinder.map

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.github.mmodzel3.lostfinder.R
import com.github.mmodzel3.lostfinder.map.overlays.UsersLocationsOverlay
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginActivity
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus
import com.github.mmodzel3.lostfinder.user.*
import kotlinx.coroutines.test.withTestContext
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker

open class UsersLocationsMapActivity : CurrentLocationMapWithCenteringActivity() {

    private lateinit var usersLocationsOverlay: UsersLocationsOverlay
    private val userEndpoint: UserEndpoint by lazy {
        UserEndpointFactory.createUserEndpoint(TokenManager.getInstance(applicationContext))
    }

    private val userEndpointViewModel: UserEndpointViewModel by viewModels {
        UserEndpointViewModelFactory(userEndpoint)
    }

    private val tokenManager: TokenManager by lazy {
        TokenManager.getInstance(applicationContext)
    }

    override fun initMap() {
        super.initMap()

        usersLocationsOverlay = UsersLocationsOverlay(map, applicationContext)
        userEndpointViewModel.users.observe(this, Observer {
            val users: Map<String, User> = it.filter {
                it.value.emailAddress != tokenManager.getTokenEmailAddress()
            }

            usersLocationsOverlay.updateUsersLocations(users)
            map.invalidate()
        })

        userEndpointViewModel.status.observe(this, Observer {
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

    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT

        startActivity(intent)
        finish()
    }
}