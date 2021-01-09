package com.github.mmodzel3.lostfinder.alert

import com.github.mmodzel3.lostfinder.location.Location
import java.util.*

data class UserAlert(val type: AlertType,
                     val location: Location?,
                     val range: Double,
                     val description: String,
                     val sendDate: Date) {
}