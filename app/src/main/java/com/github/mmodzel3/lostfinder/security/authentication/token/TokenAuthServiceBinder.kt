package com.github.mmodzel3.lostfinder.security.authentication.token

import android.os.Binder

class TokenAuthServiceBinder(private val tokenAuthService: TokenAuthService) : Binder() {
    val token: String
        get() = tokenAuthService.token
}