package com.github.mmodzel3.lostfinder.security.authentication.token

import java.io.IOException

class InvalidTokenException: IOException("Invalid account token") {
}