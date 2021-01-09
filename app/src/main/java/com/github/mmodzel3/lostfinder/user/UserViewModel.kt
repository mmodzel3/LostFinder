package com.github.mmodzel3.lostfinder.user

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.server.ServerResponse
import com.github.mmodzel3.lostfinder.server.ServerCachedViewModelAbstract

class UserViewModel(private val userRepository: UserRepository)
    : ServerCachedViewModelAbstract<User>(userRepository) {

    val users: MutableLiveData<MutableMap<String, User>>
        get() = data
    val allUsers = userRepository.allUsers

    var fetchAll: Boolean = false

    override fun runUpdates() {
        forceUpdate()
    }

    fun forceUpdate() {
        runPeriodicUpdates { fetchAllData() }
    }

    fun updateUserLocation(location: Location): LiveData<ServerResponse> {
        return convertServerRequestToLiveData { userRepository.updateUserLocation(location) }
    }

    fun clearUserLocation(): LiveData<ServerResponse> {
        return convertServerRequestToLiveData { userRepository.clearUserLocation() }
    }

    fun updateUserNotificationDestToken(token: String): LiveData<ServerResponse> {
        return convertServerRequestToLiveData { userRepository.updateUserNotificationDestToken(token) }
    }

    fun updateUserPassword(oldPassword: String,
                                   newPassword: String): LiveData<ServerResponse> {
        return convertServerRequestToLiveData { userRepository.updateUserPassword(oldPassword, newPassword) }
    }

    fun updateUserRole(email: String,
                               role: UserRole): LiveData<ServerResponse> {
        return convertServerRequestToLiveData { userRepository.updateUserRole(email, role) }
    }

    fun updateUserBlock(email: String,
                                isBlocked: Boolean): LiveData<ServerResponse> {
        return convertServerRequestToLiveData { userRepository.updateUserBlock(email, isBlocked) }
    }

    fun deleteUser(email: String = ""): LiveData<ServerResponse> {
        return convertServerRequestToLiveData { userRepository.deleteUser(email) }
    }

    internal suspend fun fetchAllData(): List<User> {
        return userRepository.getUsers(fetchAll)
    }
}