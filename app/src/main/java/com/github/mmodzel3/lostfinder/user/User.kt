package com.github.mmodzel3.lostfinder.user

import com.github.mmodzel3.lostfinder.server.ServerEndpointData
import java.time.LocalDateTime

data class User(override val id: String,
                val emailAddress: String,
                val password: String?,
                val username: String,
                val role: String,
                val longitude: Double?,
                val latitude: Double?,
                override val lastUpdateDate: LocalDateTime) : ServerEndpointData {

}