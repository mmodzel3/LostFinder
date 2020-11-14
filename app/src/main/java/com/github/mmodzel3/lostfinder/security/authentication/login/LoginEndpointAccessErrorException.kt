package com.github.mmodzel3.lostfinder.security.authentication.login

import com.github.mmodzel3.lostfinder.server.ServerEndpointAccessErrorException

class LoginEndpointAccessErrorException : ServerEndpointAccessErrorException("Login API access error") {
}
