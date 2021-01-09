package com.github.mmodzel3.lostfinder.server

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class ServerViewModelAbstract : ViewModel() {

    internal fun convertServerRequestToLiveData(serverRequestFunction: suspend () -> ServerResponse)
    : LiveData<ServerResponse> {
        val liveData = MutableLiveData<ServerResponse>()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                liveData.postValue(serverRequestFunction())
            } catch (e: InvalidTokenException) {
                liveData.postValue(ServerResponse.INVALID_TOKEN)
            } catch (e: ServerEndpointAccessErrorException) {
                liveData.postValue(ServerResponse.API_ERROR)
            }
        }

        return liveData
    }
}