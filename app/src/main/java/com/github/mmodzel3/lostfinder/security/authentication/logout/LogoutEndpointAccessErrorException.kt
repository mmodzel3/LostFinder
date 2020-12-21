package com.github.mmodzel3.lostfinder.security.authentication.logout

import com.github.mmodzel3.lostfinder.server.ServerEndpointAccessErrorException

class LogoutEndpointAccessErrorException : ServerEndpointAccessErrorException("Login API access error") {
}
