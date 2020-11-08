package com.github.mmodzel3.lostfinder.security.authentication.login

import com.github.mmodzel3.lostfinder.server.ServerEndpointAccessErrorException

class LoginAccessErrorException : ServerEndpointAccessErrorException("Login API access error") {
}