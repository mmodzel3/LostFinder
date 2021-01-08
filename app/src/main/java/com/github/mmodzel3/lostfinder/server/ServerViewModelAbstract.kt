package com.github.mmodzel3.lostfinder.server

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class ServerViewModelAbstract<T : ServerEndpointData>
    (serverRepository: ServerRepositoryAbstract<T>) : ViewModel() {

    companion object {
        const val UPDATE_INTERVALS = 60 * 1000L
        const val FAILURE_REDOWNLOAD_TIME = 40 * 1000L
    }

    open val data: MutableLiveData<MutableMap<String, T>> = serverRepository.data
    val status: MutableLiveData<ServerEndpointStatus> = MutableLiveData()
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null

    override fun onCleared() {
        super.onCleared()
        stopUpdates()
    }

    abstract fun runUpdates()

    fun stopUpdates() {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable!!)
        }
    }

    protected fun runSingleUpdate(fetchData: suspend () -> List<T>) {
        stopUpdates()
        initUpdateTask(fetchData)
        handler.post(updateRunnable!!)
    }

    protected fun runPeriodicUpdates(fetchData: suspend () -> List<T>) {
        stopUpdates()
        initPeriodicUpdateTask(fetchData)
        handler.post(updateRunnable!!)
    }

    protected fun initUpdateTask(fetchData: suspend () -> List<T>) {
        updateRunnable = Runnable {
            viewModelScope.launch {
                updateTask { fetchData() }
            }
        }
    }

    protected fun initPeriodicUpdateTask(fetchData: suspend () -> List<T>) {
        updateRunnable = Runnable {
            viewModelScope.launch {
                updateTask { fetchData() }
                handler.postDelayed(updateRunnable!!, UPDATE_INTERVALS)
            }
        }
    }

    internal open suspend fun updateTask(fetchData: suspend () -> List<T>) {
        if (status.value != ServerEndpointStatus.OK &&
            status.value != ServerEndpointStatus.FETCHING) {
            status.postValue(ServerEndpointStatus.FETCHING)
        }

        try {
            fetchData()
        } catch (e: InvalidTokenException) {
            status.postValue(ServerEndpointStatus.INVALID_TOKEN)
        } catch (e: ServerEndpointAccessErrorException) {
            status.postValue(ServerEndpointStatus.ERROR)

            if (updateRunnable != null) {
                handler.postDelayed(updateRunnable!!, FAILURE_REDOWNLOAD_TIME)
            }
        }
    }

    protected fun convertServerRequestToLiveData(serverRequestFunction: suspend () -> ServerResponse)
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