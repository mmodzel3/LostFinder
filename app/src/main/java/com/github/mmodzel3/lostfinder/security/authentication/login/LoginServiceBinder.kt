package com.github.mmodzel3.lostfinder.security.authentication.login

import android.os.Binder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoginServiceBinder(private val loginService: LoginService) : Binder() {

    suspend fun login(emailAddress: String, password: String, savePassword: Boolean = false): String {
        return withContext(Dispatchers.IO) {
            loginService.login(emailAddress, password, savePassword)
        }
    }
}
