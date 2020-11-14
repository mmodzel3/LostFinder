package com.github.mmodzel3.lostfinder.security.authentication.login

interface LoginIdlingResourceInterface {
    fun increment()
    fun decrement()
}