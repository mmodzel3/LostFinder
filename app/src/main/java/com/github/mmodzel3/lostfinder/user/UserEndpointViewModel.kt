package com.github.mmodzel3.lostfinder.user

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.github.mmodzel3.lostfinder.server.ServerEndpointViewModelAbstract

class UserEndpointViewModel(private val userEndpoint: UserEndpoint) : ServerEndpointViewModelAbstract<User>() {
    val users: MutableLiveData<MutableMap<String, User>>
        get() = data

    fun forceUpdate() {
        liveData<List<User>> {
            val userData: List<User> = userEndpoint.getUsers()
            update(userData)
        }
    }

    init {
        forceUpdate()
    }
}