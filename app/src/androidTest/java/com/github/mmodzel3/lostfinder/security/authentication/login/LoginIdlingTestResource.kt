package com.github.mmodzel3.lostfinder.security.authentication.login

import androidx.test.espresso.idling.CountingIdlingResource

class LoginIdlingTestResource : LoginIdlingResourceInterface {
    val idlingResource: CountingIdlingResource = CountingIdlingResource(LoginActivity::class.simpleName)

    override fun increment() {
        idlingResource.increment()
    }

    override fun decrement() {
        idlingResource.decrement()
    }
}