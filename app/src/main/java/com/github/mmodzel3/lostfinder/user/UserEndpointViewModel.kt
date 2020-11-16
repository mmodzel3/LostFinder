package com.github.mmodzel3.lostfinder.user

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.github.mmodzel3.lostfinder.server.ServerEndpointViewModelAbstract
import kotlinx.coroutines.launch

class UserEndpointViewModel(private val userEndpoint: UserEndpoint) : ServerEndpointViewModelAbstract<User>() {
    val users: MutableLiveData<MutableMap<String, User>>
        get() = data

    fun forceUpdate() {
        viewModelScope.launch {
            val userData: List<User> = userEndpoint.getUsers()
            update(userData)
        }
    }

    init {
        forceUpdate()
    }
}