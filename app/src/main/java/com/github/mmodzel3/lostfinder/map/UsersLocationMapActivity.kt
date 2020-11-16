package com.github.mmodzel3.lostfinder.map

import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.user.UserEndpoint
import com.github.mmodzel3.lostfinder.user.UserEndpointFactory
import com.github.mmodzel3.lostfinder.user.UserEndpointViewModel
import com.github.mmodzel3.lostfinder.user.UserEndpointViewModelFactory

open class UsersLocationMapActivity : CurrentLocationMapWithCenteringActivity() {
    private val userEndpoint: UserEndpoint =
        UserEndpointFactory.createUserEndpoint(TokenManager.getInstance(applicationContext))

    private val userEndpointViewModel: UserEndpointViewModel by viewModels {
        UserEndpointViewModelFactory(userEndpoint)
    }

    override fun initMap() {
        super.initMap()

    }


}