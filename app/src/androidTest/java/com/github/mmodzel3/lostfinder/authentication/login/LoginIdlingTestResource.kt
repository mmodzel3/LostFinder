package com.github.mmodzel3.lostfinder.authentication.login

import androidx.test.espresso.idling.CountingIdlingResource
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginActivity
import com.github.mmodzel3.lostfinder.security.authentication.login.LoginIdlingResourceInterface

class LoginIdlingTestResource : LoginIdlingResourceInterface {
    val idlingResource: CountingIdlingResource = CountingIdlingResource(LoginActivity::class.simpleName)

    override fun increment() {
        idlingResource.increment()
    }

    override fun decrement() {
        idlingResource.decrement()
    }
}