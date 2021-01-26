package com.github.mmodzel3.lostfinder.weather

import java.io.IOException

open class WeatherEndpointAccessErrorException(message: String) : IOException(message) {

    constructor() : this("Weather endpoint access error.") {
    }
}
