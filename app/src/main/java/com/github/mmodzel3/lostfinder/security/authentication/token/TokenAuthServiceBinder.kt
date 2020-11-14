package com.github.mmodzel3.lostfinder.security.authentication.token

import android.os.Binder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TokenAuthServiceBinder(private val tokenAuthService: TokenAuthService) : Binder() {
    suspend fun getToken() : String {
        return withContext(Dispatchers.IO) {
            tokenAuthService.getToken()
        }
    }
}