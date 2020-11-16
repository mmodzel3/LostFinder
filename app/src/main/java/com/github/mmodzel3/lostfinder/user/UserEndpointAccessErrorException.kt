package com.github.mmodzel3.lostfinder.user

import com.github.mmodzel3.lostfinder.server.ServerEndpointAccessErrorException

class UserEndpointAccessErrorException : ServerEndpointAccessErrorException("User API access error") {
}
