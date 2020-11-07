package com.github.mmodzel3.lostfinder.security.authentication.login.service

import android.os.Binder

class LoginServiceBinder(private val loginService: LoginService) : Binder() {

    fun login(emailAddress: String, password: String): String {
        return loginService.login(emailAddress, password)
    }
}