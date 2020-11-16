package com.github.mmodzel3.lostfinder.server

import java.time.LocalDateTime

interface ServerEndpointData {
    val id: String
    val lastUpdateDate: LocalDateTime
}