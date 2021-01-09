package com.github.mmodzel3.lostfinder.security.authentication.register

import com.github.mmodzel3.lostfinder.security.authentication.login.LoginRepository
import com.github.mmodzel3.lostfinder.server.ServerRepositoryAbstract
import com.github.mmodzel3.lostfinder.server.ServerResponse

class RegisterRepository private constructor() : ServerRepositoryAbstract() {
    companion object {
        private var registerRepository: RegisterRepository? = null

        fun getInstance(): RegisterRepository {
            if (registerRepository == null) {
                registerRepository = RegisterRepository()
            }

            return registerRepository!!
        }

        fun clear() {
            registerRepository = null
        }
    }

    private val registerEndpoint: RegisterEndpoint by lazy {
        RegisterEndpointFactory.createRegisterEndpoint()
    }

    suspend fun register(emailAddress: String,
                         password : String,
                         serverPassword : String = "",
                         username: String): ServerResponse {
        return registerEndpoint.register(emailAddress, password, serverPassword, username)
    }
}