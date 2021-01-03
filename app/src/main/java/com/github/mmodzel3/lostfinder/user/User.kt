package com.github.mmodzel3.lostfinder.user

import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.server.ServerEndpointLocationData
import java.util.*

data class User(override val id: String,
                val email: String,
                val password: String?,
                val username: String,
                val role: UserRole,
                override val location: Location?,
                override val lastUpdateDate: Date,
                val lastLoginDate: Date?,
                val blocked: Boolean,
                val deleted: Boolean,
                val notificationDestToken: String?) : ServerEndpointLocationData {

    fun isUser() : Boolean {
        return true
    }

    fun isManager() : Boolean {
        return role.isManager()
    }

    fun isOwner() : Boolean {
        return role.isOwner()
    }
}