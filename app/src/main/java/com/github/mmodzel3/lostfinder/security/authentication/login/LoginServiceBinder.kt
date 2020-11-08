package com.github.mmodzel3.lostfinder.security.authentication.login

import android.os.Binder
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginService

class LoginServiceBinder(private val loginService: LoginService) : Binder() {

    fun login(emailAddress: String, password: String): String {
        return loginService.login(emailAddress, password)
    }

    fun sendLoginRequestAndGetToken(emailAddress: String, password: String): String {
        return loginService.sendLoginRequestAndGetToken(emailAddress, password)
    }
}