package com.github.mmodzel3.lostfinder.server

import androidx.lifecycle.LiveData
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class ServerViewModelAbstractTest {
    private lateinit var serverViewModelAbstract: ServerViewModelAbstractImpl
    private lateinit var latch: CountDownLatch

    @Before
    fun setUp() {
        latch = CountDownLatch(1)
        serverViewModelAbstract = ServerViewModelAbstractImpl()
    }

    @Test
    fun whenConvertServerRequestToLiveDataThenItIsConverted() {
        val response: LiveData<ServerResponse> = serverViewModelAbstract.convertServerRequestToLiveData {
            serverNormalResponse()
        }

        val serverResponse: ServerResponse = observeLiveData(response)

        assertThat(serverResponse).isEqualTo(ServerResponse.OK)
    }

    @Test
    fun whenConvertServerRequestToLiveDataAndApiAccessErrorThenItIsConvertedToError() {
        val response: LiveData<ServerResponse> = serverViewModelAbstract.convertServerRequestToLiveData {
            serverApiErrorResponse()
        }

        val serverResponse: ServerResponse = observeLiveData(response)

        assertThat(serverResponse).isEqualTo(ServerResponse.API_ERROR)
    }

    @Test
    fun whenConvertServerRequestToLiveDataAndInvalidTokenErrorThenItIsConvertedToError() {
        val response: LiveData<ServerResponse> = serverViewModelAbstract.convertServerRequestToLiveData {
            serverInvalidTokenResponse()
        }

        val serverResponse: ServerResponse = observeLiveData(response)

        assertThat(serverResponse).isEqualTo(ServerResponse.INVALID_TOKEN)
    }

    private fun serverNormalResponse(): ServerResponse {
        return ServerResponse.OK
    }

    private fun serverInvalidTokenResponse(): ServerResponse {
         throw InvalidTokenException()
    }

    private fun serverApiErrorResponse(): ServerResponse {
        throw ServerEndpointAccessErrorException()
    }

    private fun observeLiveData(liveData: LiveData<ServerResponse>): ServerResponse {
        var serverResponse: ServerResponse? = null

        runBlocking(Dispatchers.Main) {
            liveData.observeForever {
                serverResponse = it
                latch.countDown()
            }
        }

        latch.await(1000, TimeUnit.MILLISECONDS)

        return serverResponse!!
    }
}