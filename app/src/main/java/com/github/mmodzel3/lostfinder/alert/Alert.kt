package com.github.mmodzel3.lostfinder.alert

import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.server.ServerEndpointData
import com.github.mmodzel3.lostfinder.user.User
import java.util.*

data class Alert(override val id: String,
                 val type: String,
                 val user: User,
                 val location: Location?,
                 val range: Double,
                 val title: String,
                 val description: String,
                 val showNotificationAtStart: Boolean,
                 val showNotificationAtEnd: Boolean,
                 val sendDate: Date,
                 val receivedDate: Date,
                 val endDate: Date?,
                 override val lastUpdateDate: Date) : ServerEndpointData {
}