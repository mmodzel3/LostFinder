package com.github.mmodzel3.lostfinder.map.overlays

import android.content.Context
import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.server.ServerEndpointLocationDataImpl
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.MapViewRepository
import org.osmdroid.views.overlay.Marker
import java.util.*
import kotlin.collections.HashMap

class DataLocationsOverlayTest {
    companion object {
        const val DAY_BEFORE_IN_MILLISECONDS = 24*60*60*1000

        const val TEST_LONGITUDE = 22.3
        const val TEST_LATITUDE = 21.4
    }

    private lateinit var mapView: MapView
    private lateinit var context: Context
    private lateinit var dataLocationsOverlay: DataLocationsOverlay<ServerEndpointLocationDataImpl>
    private lateinit var data: MutableMap<String, ServerEndpointLocationDataImpl>

    @Before
    fun setUp() {
        mockContext()
        mockMapView()

        dataLocationsOverlay = DataLocationsOverlay(mapView)
        createTestData()
    }

    @Test
    fun whenUpdateDataLocationsAndDataIsNotCachedThenItIsAdded() {
        dataLocationsOverlay.updateDataLocations(data)

        checkDataLocations()
    }

    @Test
    fun whenUpdateDataLocationsAndDataIsCachedThenItIsUpdated() {
        dataLocationsOverlay.markers.putAll(createTestMarkersFromTestData())
        updateTestData()
        dataLocationsOverlay.updateDataLocations(data)

        checkDataLocations()
    }

    @Test
    fun whenUpdateDataLocationsAndNoSpecificDataThenItIsRemoved() {
        dataLocationsOverlay.markers.putAll(createTestMarkersFromTestData())
        removeOneElementFromTestData()
        dataLocationsOverlay.updateDataLocations(data)

        assertThat(dataLocationsOverlay.markers).hasSize(data.size)

        checkDataLocations()
    }

    private fun mockMapView() {
        val mapViewRepository: MapViewRepository = mock(MapViewRepository::class.java)
        mapView = mock(MapView::class.java)

        Mockito.`when`(mapView.getContext()).thenReturn(context)
        Mockito.`when`(mapView.getRepository()).thenReturn(mapViewRepository)
    }

    private fun mockContext() {
        context = mock(Context::class.java)
    }

    private fun createTestData() {
        val yesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS)

        data = HashMap()
        for (i in 1..10) {
            val elem = ServerEndpointLocationDataImpl(i.toString(), Location(TEST_LATITUDE, TEST_LONGITUDE), yesterday)

            data[i.toString()] = elem
        }
    }

    private fun checkDataLocations() {
        assertThat(dataLocationsOverlay.markers).hasSize(data.size)

        data.forEach {
            assertThat(dataLocationsOverlay.markers).containsKey(it.key)

            val marker: Marker = dataLocationsOverlay.markers[it.key]!!
            assertThat(marker.position.latitude).isEqualTo(it.value.location?.latitude)
            assertThat(marker.position.longitude).isEqualTo(it.value.location?.longitude)
        }
    }

    private fun updateTestData(): MutableMap<String, ServerEndpointLocationDataImpl> {
        val newData : MutableMap<String, ServerEndpointLocationDataImpl> = HashMap()

        data.forEach {
            val newElem = ServerEndpointLocationDataImpl(it.value.id, Location(TEST_LATITUDE, TEST_LONGITUDE), Date())

            newData[it.key] = newElem
        }

        data = newData
        return newData
    }

    private fun createTestMarkersFromTestData() : MutableMap<String, Marker> {
        val dataMarkers : MutableMap<String, Marker> = HashMap()

        data.forEach {
            val marker: Marker = Marker(mapView)
            val location: Location = it.value.location!!
            marker.position = GeoPoint(location.latitude, location.longitude)
            dataMarkers[it.key] = marker
        }

        return dataMarkers
    }

    private fun removeOneElementFromTestData() {
        val dataIdToRemove: String = data.keys.random()
        data.remove(dataIdToRemove)
    }
}