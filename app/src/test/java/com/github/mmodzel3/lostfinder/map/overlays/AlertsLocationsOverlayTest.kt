package com.github.mmodzel3.lostfinder.map.overlays

import android.content.Context
import android.content.res.Resources
import com.github.mmodzel3.lostfinder.alert.Alert
import com.github.mmodzel3.lostfinder.alert.AlertEndpointTestAbstract
import com.github.mmodzel3.lostfinder.alert.AlertRepositoryTestAbstract
import com.github.mmodzel3.lostfinder.user.User
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.mock
import org.osmdroid.views.MapView
import org.osmdroid.views.MapViewRepository

class AlertsLocationsOverlayTest : AlertRepositoryTestAbstract() {
    companion object {
        const val ALERT_TITLE = "Help!"
    }

    private lateinit var mapView: MapView
    private lateinit var context: Context
    private lateinit var alertsLocationsOverlay: AlertsLocationsOverlay

    @Before
    override fun setUp() {
        super.setUp()
        mockContext()
        mockMapView()

        alertsLocationsOverlay = AlertsLocationsOverlay(mapView, context)
        createTestAlerts()
    }

    @Test
    fun whenUpdateAlertsLocationsAndDataIsNotCachedThenItIsAddedWithCorrectTitles() {
        val alertsMap: Map<String, Alert> = convertAlertsToMap()
        alertsLocationsOverlay.updateDataLocations(alertsMap)

        alertsLocationsOverlay.markers.forEach {
            val user: User = alertsMap[it.key]!!.user
            assertThat(it.value.title).isEqualTo(ALERT_TITLE + " [" + user.username + "]")
        }
    }

    private fun mockMapView() {
        val mapViewRepository: MapViewRepository = mock(MapViewRepository::class.java)
        mapView = mock(MapView::class.java)

        Mockito.`when`(mapView.getContext()).thenReturn(context)
        Mockito.`when`(mapView.getRepository()).thenReturn(mapViewRepository)
    }

    private fun mockContext() {
        val resources: Resources = mock(Resources::class.java)
        context = mock(Context::class.java)

        Mockito.`when`(context.resources).thenReturn(resources)
        Mockito.`when`(context.getString(anyInt())).thenReturn(ALERT_TITLE)
        Mockito.`when`(resources.getStringArray(anyInt())).thenReturn(listOf<String>(ALERT_TITLE).toTypedArray())
    }

    private fun convertAlertsToMap() : Map<String, Alert> {
        val alertsMap: MutableMap<String, Alert> = HashMap()

        alerts.forEach {
            alertsMap[it.id] = it
        }

        return alertsMap
    }
}