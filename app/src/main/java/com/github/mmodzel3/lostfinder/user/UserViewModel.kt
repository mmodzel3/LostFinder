package com.github.mmodzel3.lostfinder.user

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.server.ServerResponse
import com.github.mmodzel3.lostfinder.server.ServerViewModelAbstract

class UserViewModel(private val userRepository: UserRepository)
    : ServerViewModelAbstract<User>(userRepository) {

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

    suspend fun updateUserLocation(location: Location): ServerResponse {
        return userRepository.updateUserLocation(location)
    }

    suspend fun clearUserLocation(): ServerResponse {
        return userRepository.clearUserLocation()
    }

    suspend fun updateUserNotificationDestToken(token: String): ServerResponse {
        return userRepository.updateUserNotificationDestToken(token)
    }

    suspend fun updateUserPassword(oldPassword: String,
                                   newPassword: String): ServerResponse {
        return userRepository.updateUserPassword(oldPassword, newPassword)
    }

    suspend fun updateUserRole(email: String,
                               role: UserRole): ServerResponse {
        return userRepository.updateUserRole(email, role)
    }

    suspend fun updateUserBlock(email: String,
                                isBlocked: Boolean): ServerResponse {
        return userRepository.updateUserBlock(email, isBlocked)
    }

    suspend fun deleteUser(email: String = ""): ServerResponse {
        return userRepository.deleteUser(email)
    }

    internal suspend fun fetchAllData(): List<User> {
        return userRepository.getUsers(fetchAll)
    }
}