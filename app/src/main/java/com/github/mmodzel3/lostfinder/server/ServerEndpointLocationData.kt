package com.github.mmodzel3.lostfinder.server

import com.github.mmodzel3.lostfinder.location.Location

interface ServerEndpointLocationData : ServerEndpointData {
    val location: Location?
}