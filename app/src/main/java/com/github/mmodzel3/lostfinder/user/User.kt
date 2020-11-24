package com.github.mmodzel3.lostfinder.user

import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.server.ServerEndpointData
import java.util.*

data class User(override val id: String,
                val email: String,
                val password: String?,
                val username: String,
                val role: String,
                val location: Location?,
                override val lastUpdateDate: Date) : ServerEndpointData {

    fun isUser() : Boolean {
        return role == "USER"
    }

    fun isAdmin() : Boolean {
        return role == "ADMIN"
    }

    fun isOwner() : Boolean {
        return role == "OWNER"
    }
}