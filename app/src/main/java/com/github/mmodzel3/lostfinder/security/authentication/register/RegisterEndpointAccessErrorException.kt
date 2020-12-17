package com.github.mmodzel3.lostfinder.security.authentication.register

import com.github.mmodzel3.lostfinder.server.ServerEndpointAccessErrorException

class RegisterEndpointAccessErrorException : ServerEndpointAccessErrorException("Login API access error") {
}
