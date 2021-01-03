package com.github.mmodzel3.lostfinder.server

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class ServerEndpointViewModelAbstract<T : ServerEndpointData> : ViewModel() {
    companion object {
        const val UPDATE_INTERVALS = 60 * 1000L
    }

    val status: MutableLiveData<ServerEndpointStatus> = MutableLiveData()
    protected open val data: MutableLiveData<MutableMap<String, T>> = MutableLiveData()
    internal val dataCache: MutableMap<String, T> = mutableMapOf()
    internal val lock = Any()
    private lateinit var handler: Handler
    private lateinit var updateRunnable: Runnable

    fun forceUpdate() {
        viewModelScope.launch {
            if (status.value != ServerEndpointStatus.OK &&
                    status.value != ServerEndpointStatus.FETCHING) {
                status.postValue(ServerEndpointStatus.FETCHING)
            }

            fetchAllData()
        }
    }

    internal open suspend fun fetchAllData() {

    }

    protected fun runPeriodicUpdates() {
        handler = Handler(Looper.getMainLooper())
        updateRunnable = Runnable {
            forceUpdate()
            handler.postDelayed(updateRunnable, UPDATE_INTERVALS)
        }

        status.postValue(ServerEndpointStatus.FETCHING)
        handler.post(updateRunnable)
    }

    protected fun stopPeriodicUpdates() {
        handler.removeCallbacks(updateRunnable)
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