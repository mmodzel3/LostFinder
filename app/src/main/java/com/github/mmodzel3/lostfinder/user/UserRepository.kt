package com.github.mmodzel3.lostfinder.user

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerRepositoryAbstract
import com.github.mmodzel3.lostfinder.server.ServerResponse

class UserRepository private constructor(private val tokenManager: TokenManager?) : ServerRepositoryAbstract<User>() {
    companion object {
        var userRepository: UserRepository? = null

        fun getInstance(tokenManager: TokenManager?): UserRepository {
            if (userRepository == null) {
                userRepository = UserRepository(tokenManager)
            }

            return userRepository!!
        }

        fun clear() {
            userRepository = null
        }
    }

    val users: MutableLiveData<MutableMap<String, User>>
        get() = data
    val allUsers = MutableLiveData<MutableMap<String, User>>()

    private val userEndpoint: UserEndpoint by lazy {
        UserEndpointFactory.createUserEndpoint(tokenManager)
    }

    suspend fun getUsers(fetchAll: Boolean): List<User> {
        return fetchAndUpdate { userEndpoint.getUsers(fetchAll) }
    }

    suspend fun updateUserLocation(location: Location): ServerResponse {
        return userEndpoint.updateUserLocation(location)
    }

    suspend fun clearUserLocation(): ServerResponse {
        return userEndpoint.clearUserLocation()
    }

    suspend fun updateUserNotificationDestToken(token: String): ServerResponse {
        return userEndpoint.updateUserNotificationDestToken(token)
    }

    suspend fun updateUserPassword(oldPassword: String,
                                   newPassword: String): ServerResponse {
        return userEndpoint.updateUserPassword(oldPassword, newPassword)
    }

    suspend fun updateUserRole(email: String,
                               role: UserRole): ServerResponse {
        return userEndpoint.updateUserRole(email, role)
    }

    suspend fun updateUserBlock(email: String,
                                isBlocked: Boolean): ServerResponse {
        return userEndpoint.updateUserBlock(email, isBlocked)
    }

    suspend fun deleteUser(email: String = ""): ServerResponse {
        return userEndpoint.deleteUser(email)
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
        }
    }
}