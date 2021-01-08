package com.github.mmodzel3.lostfinder.server

import android.util.Log
import androidx.lifecycle.MutableLiveData

abstract class ServerRepositoryAbstract<T: ServerEndpointData> {
    internal open val data: MutableLiveData<MutableMap<String, T>> = MutableLiveData()
    internal val dataCache: MutableMap<String, T> = mutableMapOf()
    protected val lock = Any()

    internal open suspend fun fetchAndUpdate(fetchData: suspend () -> List<T>): List<T> {
        val data: List<T> = fetchData()
        update(data)

        return data
    }

    internal open fun update(dataToUpdate: List<T>) {
        synchronized(lock) {
            if (updateCache(dataToUpdate)) {
                data.postValue(dataCache)
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