package com.github.mmodzel3.lostfinder.security.authentication.register

import androidx.lifecycle.LiveData
import com.github.mmodzel3.lostfinder.server.ServerResponse
import com.github.mmodzel3.lostfinder.server.ServerViewModelAbstract

class RegisterViewModel(private val registerRepository: RegisterRepository) : ServerViewModelAbstract() {

    fun register(emailAddress: String,
                         password : String,
                         serverPassword : String = "",
                         username: String): LiveData<ServerResponse> {

        return convertServerRequestToLiveData {
            registerRepository.register(emailAddress, password, serverPassword, username)
        }
    }
}