package com.github.mmodzel3.lostfinder.map.overlays

import android.content.Context
import com.github.mmodzel3.lostfinder.user.User
import com.github.mmodzel3.lostfinder.user.UserEndpointTestAbstract
import com.github.mmodzel3.lostfinder.user.UserRepositoryTestAbstract
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.osmdroid.views.MapView
import org.osmdroid.views.MapViewRepository

class UsersLocationsOverlayTest : UserRepositoryTestAbstract() {
    companion object {
        const val USER_EMAIL = "example@example.com"
        const val USER_NAME = "example"
        const val USER_ROLE = "ADMIN"

        const val TEST_LONGITUDE = 22.3
        const val TEST_LATITUDE = 21.4
    }

    private lateinit var mapView: MapView
    private lateinit var context: Context
    private lateinit var usersLocationsOverlay: UsersLocationsOverlay

    @Before
    override fun setUp() {
        super.setUp()
        mockContext()
        mockMapView()

        usersLocationsOverlay = UsersLocationsOverlay(mapView, context)
        createTestUsers()
    }

    @Test
    fun whenUpdateUsersLocationsAndDataIsNotCachedThenItIsAddedWithCorrectTitles() {
        val usersMap: Map<String, User> = convertUsersToMap()
        usersLocationsOverlay.updateDataLocations(usersMap)

        usersLocationsOverlay.markers.forEach {
            val user: User = usersMap[it.key]!!
            val username: String = user.username
            assertThat(it.value.title).isEqualTo("$username\n[$USER_ROLE]")
        }
    }

    private fun mockMapView() {
        val mapViewRepository: MapViewRepository = mock(MapViewRepository::class.java)
        mapView = mock(MapView::class.java)

        Mockito.`when`(mapView.getContext()).thenReturn(context)
        Mockito.`when`(mapView.getRepository()).thenReturn(mapViewRepository)
    }

    private fun mockContext() {
        context = mock(Context::class.java)

        Mockito.`when`(context.getString(Mockito.anyInt())).thenReturn(USER_ROLE)
    }

    private fun convertUsersToMap() : Map<String, User> {
        val usersMap: MutableMap<String, User> = HashMap()

        users.forEach {
            usersMap[it.id] = it
        }

        return usersMap
    }
}