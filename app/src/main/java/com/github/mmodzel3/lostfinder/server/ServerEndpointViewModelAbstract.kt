package com.github.mmodzel3.lostfinder.server

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

abstract class ServerEndpointViewModelAbstract<T : ServerEndpointData> : ViewModel() {
    val status: MutableLiveData<ServerEndpointStatus> = MutableLiveData()
    protected val data: MutableLiveData<MutableMap<String, T>> = MutableLiveData()
    private val dataCache: MutableMap<String, T> = mutableMapOf()
    private val lock = Any()

    fun forceUpdate() {
        viewModelScope.launch {
            status.postValue(ServerEndpointStatus.FETCHING)
            fetchAllData()
        }
    }

    protected open suspend fun fetchAllData() {

    }

    protected fun update(dataToUpdate: List<T>) {
        synchronized(lock) {
            updateCache(dataToUpdate)

            if (status.value != ServerEndpointStatus.OK) {
                status.postValue(ServerEndpointStatus.OK)
            }
        }
    }

    private fun updateCache(dataToUpdate: List<T>) {
        var dataChanged = false

        dataToUpdate.forEach {
            val cachedElement: T? = dataCache[it.id]

            if (cachedElement != null) {
                dataChanged = dataChanged || updateElementIfNecessary(cachedElement, it)
            } else {
                dataChanged = true
                addElement(it)
            }
        }

        if (dataChanged) {
            data.postValue(dataCache)
        }
    }

    private fun updateElementIfNecessary(cachedElement: T, elementToUpdate: T): Boolean {
        return if (cachedElement.lastUpdateDate <= elementToUpdate.lastUpdateDate) {
            dataCache.remove(elementToUpdate.id)
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