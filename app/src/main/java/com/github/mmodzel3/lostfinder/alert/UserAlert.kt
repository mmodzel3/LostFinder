package com.github.mmodzel3.lostfinder.alert

import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.user.User
import java.util.*

data class UserAlert(val type: String,
                     val location: Location?,
                     val range: Double,
                     val title: String,
                     val description: String,
                     val showNotificationAtStart: Boolean,
                     val showNotificationAtEnd: Boolean,
                     val sendDate: Date) {
}