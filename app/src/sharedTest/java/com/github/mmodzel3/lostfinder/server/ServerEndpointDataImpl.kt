package com.github.mmodzel3.lostfinder.server

import java.util.*

data class ServerEndpointDataImpl(override val id: String,
                                  override val lastUpdateDate: Date) : ServerEndpointData {
}