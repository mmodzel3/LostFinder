package com.github.mmodzel3.lostfinder.security.authentication.login.exceptions

import com.github.mmodzel3.lostfinder.server.ServerEndpointAccessErrorException

class LoginAccessErrorException : ServerEndpointAccessErrorException("Login API access error") {
}