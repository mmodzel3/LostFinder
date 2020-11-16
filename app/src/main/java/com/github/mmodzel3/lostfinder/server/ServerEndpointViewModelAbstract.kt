package com.github.mmodzel3.lostfinder.server

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class ServerEndpointViewModelAbstract<T : ServerEndpointData> : ViewModel() {
    protected val data: MutableLiveData<Collection<T>> = MutableLiveData()
    private val dataCache: MutableMap<String, T> = mutableMapOf()
    private val lock = Any()

    protected fun update(dataToUpdate: List<T>) {
        synchronized(lock) {
            updateCache(dataToUpdate)
        }
    }

    private fun updateCache(dataToUpdate: List<T>) {
        var dataChanged = false

        dataToUpdate.forEach {
            val cachedElement: T? = dataCache[it.id]

            if (cachedElement != null) {
                dataChanged = updateElementIfNecessary(cachedElement, it)
            } else {
                dataChanged = true
                addElement(it)
            }
        }

        if (dataChanged) {
            data.postValue(dataCache.values)
        }
    }

    private fun updateElementIfNecessary(cachedElement: T, elementToUpdate: T): Boolean {
        return if (cachedElement.updateDate <= elementToUpdate.updateDate) {
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