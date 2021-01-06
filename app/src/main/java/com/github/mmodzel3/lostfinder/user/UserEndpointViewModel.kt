package com.github.mmodzel3.lostfinder.user

import androidx.lifecycle.MutableLiveData
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus
import com.github.mmodzel3.lostfinder.server.ServerEndpointViewModelAbstract

class UserEndpointViewModel(private val userEndpoint: UserEndpoint) : ServerEndpointViewModelAbstract<User>() {
    val users: MutableLiveData<MutableMap<String, User>>
        get() = data
    val allUsers = MutableLiveData<MutableMap<String, User>>()

    var fetchAll: Boolean = false

    override fun observeUpdates() {
        forceUpdate()
    }

    override fun unObserveUpdates() {
        stopUpdates()
    }

    fun forceUpdate() {
        runPeriodicUpdates { fetchAllData() }
    }

    override fun update(dataToUpdate: List<User>) {
        synchronized(lock) {
            dataCache.clear()

            if (updateCache(dataToUpdate)) {
                data.postValue(dataCache.filter {
                    !it.value.blocked && !it.value.deleted
                }.toMutableMap())

                allUsers.postValue(dataCache)
            }

            if (status.value != ServerEndpointStatus.OK) {
                status.postValue(ServerEndpointStatus.OK)
            }
        }
    }

    internal suspend fun fetchAllData(): List<User> {
        return userEndpoint.getUsers(fetchAll)
    }
}