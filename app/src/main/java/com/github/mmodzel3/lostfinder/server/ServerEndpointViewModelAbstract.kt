package com.github.mmodzel3.lostfinder.server

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import kotlinx.coroutines.launch

abstract class ServerEndpointViewModelAbstract<T : ServerEndpointData> : ViewModel() {
    companion object {
        const val UPDATE_INTERVALS = 60 * 1000L
    }

    val status: MutableLiveData<ServerEndpointStatus> = MutableLiveData()
    protected open val data: MutableLiveData<MutableMap<String, T>> = MutableLiveData()
    internal val dataCache: MutableMap<String, T> = mutableMapOf()
    internal val lock = Any()
    private val handler: Handler = Handler(Looper.getMainLooper())
    private var updateRunnable: Runnable? = null

    override fun onCleared() {
        super.onCleared()
        stopUpdates()
    }

    protected fun stopUpdates() {
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable!!)
        }
    }

    protected fun runUpdate(fetchData: suspend () -> List<T>) {
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
            update(fetchData())
        } catch (e: InvalidTokenException) {
            status.postValue(ServerEndpointStatus.INVALID_TOKEN)
        } catch (e: ServerEndpointAccessErrorException) {
            status.postValue(ServerEndpointStatus.ERROR)

            handler.postDelayed(updateRunnable!!, UPDATE_INTERVALS)
        }
    }

    internal open fun update(dataToUpdate: List<T>) {
        synchronized(lock) {
            if (updateCache(dataToUpdate)) {
                data.postValue(dataCache)
            }

            if (status.value != ServerEndpointStatus.OK) {
                status.postValue(ServerEndpointStatus.OK)
            }
        }
    }

    internal open fun updateCache(dataToUpdate: List<T>): Boolean {
        var dataChanged = false

        dataToUpdate.forEach {
            val cachedElement: T? = dataCache[it.id]

            if (cachedElement != null) {
                val updateMade: Boolean = updateElementIfNecessary(cachedElement, it)
                dataChanged = updateMade || dataChanged
            } else {
                dataChanged = true
                addElement(it)
            }
        }

        return dataChanged
    }

    private fun updateElementIfNecessary(cachedElement: T, elementToUpdate: T): Boolean {
        return if (cachedElement.lastUpdateDate.before(elementToUpdate.lastUpdateDate)) {
            dataCache[elementToUpdate.id] = elementToUpdate
            true
        } else {
            false
        }
    }

    private fun addElement(element: T) {
        dataCache[element.id] = element
    }
}