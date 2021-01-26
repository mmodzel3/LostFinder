package com.github.mmodzel3.lostfinder.server

import com.github.mmodzel3.lostfinder.location.Location
import java.util.*

data class ServerEndpointLocationDataImpl(override val id: String,
                                          override val location: Location?,
                                          override val lastUpdateDate: Date) : ServerEndpointLocationData {
}