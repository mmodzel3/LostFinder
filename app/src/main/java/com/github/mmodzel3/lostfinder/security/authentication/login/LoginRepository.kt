package com.github.mmodzel3.lostfinder.security.authentication.login

import com.github.mmodzel3.lostfinder.server.ServerRepositoryAbstract

class LoginRepository private constructor() : ServerRepositoryAbstract() {
    companion object {
        private var loginRepository: LoginRepository? = null

        fun getInstance(): LoginRepository {
            if (loginRepository == null) {
                loginRepository = LoginRepository()
            }

            return loginRepository!!
        }

        fun clear() {
            loginRepository = null
        }
    }

    private val loginEndpoint: LoginEndpoint by lazy {
        LoginEndpointFactory.createLoginEndpoint()
    }

    suspend fun login(emailAddress: String,
                      password : String,
                      pushNotificationDestToken : String?): LoginInfo {
        return loginEndpoint.login(emailAddress, password, pushNotificationDestToken)
    }
}