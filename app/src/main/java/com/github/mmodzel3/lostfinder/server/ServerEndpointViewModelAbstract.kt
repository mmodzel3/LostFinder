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
        dataToUpdate.forEach {
            val cachedElement: T? = dataCache[it.id]

            if (cachedElement != null) {
                updateElementIfNecessary(cachedElement, it)
            } else {
                addElement(it)
            }
        }

        data.postValue(dataCache.values)
    }

    private fun updateElementIfNecessary(cachedElement: T, elementToUpdate: T) {
        if (cachedElement.updateDate <= elementToUpdate.updateDate) {
            dataCache.remove(elementToUpdate.id)
            dataCache[elementToUpdate.id] = elementToUpdate
        }
    }

    private fun addElement(element: T) {
        dataCache[element.id] = element
    }
}