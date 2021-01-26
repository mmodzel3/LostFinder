package com.github.mmodzel3.lostfinder.alert

import com.github.mmodzel3.lostfinder.server.ServerEndpointAccessErrorException

class AlertEndpointAccessErrorException : ServerEndpointAccessErrorException("Alert API access error") {
}
