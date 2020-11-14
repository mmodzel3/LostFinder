package com.github.mmodzel3.lostfinder.server

import java.io.IOException

open class ServerEndpointAccessErrorException(message: String) : IOException(message) {

    constructor() : this("Server endpoint access error.") {
    }
}
