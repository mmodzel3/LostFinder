package com.github.mmodzel3.lostfinder.map.overlays

import com.github.mmodzel3.lostfinder.server.ServerEndpointLocationData
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker


open class DataLocationsOverlay<T: ServerEndpointLocationData>(private val map: MapView): FolderOverlay() {
    internal val markers: MutableMap<String, Marker> = mutableMapOf()
    private val lock: Any = Any()

    fun updateDataLocations(data: Map<String, T>) = synchronized(lock) {
        data.forEach {
            updateDataLocation(it.value)
        }

        if (markers.size != data.size) {
            removeOldData(data)
        }
    }

    protected fun updateDataLocation(data: T) {
        if (markers[data.id] == null) {
            addMarker(data)
        } else {
            updateMarker(data)
        }
    }

    protected fun addMarker(data: T) {
        if (data.location != null) {
            val marker: Marker = createMarker(data)
            marker.position = GeoPoint(data.location!!.latitude, data.location!!.longitude)

            markers[data.id] = marker
            add(marker)
        }
    }

    protected open fun createMarker(data: T): Marker {
        return Marker(map)
    }

    protected fun updateMarker(data: T) {
        if (data.location != null) {
            val marker: Marker = markers[data.id]!!
            marker.position = GeoPoint(data.location!!.latitude, data.location!!.longitude)
            markers[data.id] = marker
        } else {
            removeMarker(data)
        }
    }

    private fun removeMarker(data: T) {
        removeMarker(data.id)
    }

    private fun removeMarker(dataId: String) {
        val marker: Marker = markers[dataId]!!
        markers.remove(dataId)
        remove(marker)
    }

    private fun removeOldData(data: Map<String, T>) {
        val keysToRemove: MutableList<String> = ArrayList()

        markers.forEach {
            if(it.key !in data.keys) {
                keysToRemove.add(it.key)
            }
        }

        keysToRemove.forEach {
            markers.remove(it)
        }
    }
}