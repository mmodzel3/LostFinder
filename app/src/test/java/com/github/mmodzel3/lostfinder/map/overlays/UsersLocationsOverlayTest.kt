package com.github.mmodzel3.lostfinder.map.overlays

import android.content.Context
import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.user.User
import com.github.mmodzel3.lostfinder.user.UserEndpointTestAbstract
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

class UsersLocationsOverlayTest {
    companion object {
        const val USER_EMAIL = "example@example.com"
        const val USER_NAME = "example"
        const val USER_ROLE = "ADMIN"
        const val DAY_BEFORE_IN_MILLISECONDS = 24*60*60*1000

        const val TEST_LONGITUDE = 22.3
        const val TEST_LATITUDE = 21.4

        const val TEST_LONGITUDE2 = 21.3
        const val TEST_LATITUDE2 = 23.4
    }

    private lateinit var mapView: MapView
    private lateinit var context: Context
    private lateinit var usersLocationsOverlay: UsersLocationsOverlay
    private lateinit var users: MutableMap<String, User>

    @Before
    fun setUp() {
        mockContext()
        mockMapView()

        usersLocationsOverlay = UsersLocationsOverlay(mapView, context)
        createTestUsers()
    }

    @Test
    fun whenUpdateUsersLocationsAndDataIsNotCachedThenItIsAdded() {
        usersLocationsOverlay.updateUsersLocations(users)

        checkUsersLocations()
    }

    @Test
    fun whenUpdateUsersLocationsAndDataIsCachedThenItIsUpdated() {
        usersLocationsOverlay.usersMarkers.putAll(createTestMarkersFromTestUsers())
        updateTestUsers()
        usersLocationsOverlay.updateUsersLocations(users)

        checkUsersLocations()
    }

    @Test
    fun whenUpdateUsersLocationsAndNoSpecificUserThenItIsRemoved() {
        usersLocationsOverlay.usersMarkers.putAll(createTestMarkersFromTestUsers())
        removeOneOfTestUsers()
        usersLocationsOverlay.updateUsersLocations(users)

        assertThat(usersLocationsOverlay.usersMarkers).hasSize(users.size)

        checkUsersLocations()
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

    private fun createTestUsers() {
        val yesterday = Date(System.currentTimeMillis() - UserEndpointTestAbstract.DAY_BEFORE_IN_MILLISECONDS)

        users = HashMap()
        for (i in 1..10) {
            val user = User(i.toString(), USER_EMAIL + i.toString(), null,
                    USER_NAME, USER_ROLE, Location(TEST_LONGITUDE, TEST_LATITUDE), yesterday)

            users[i.toString()] = user
        }
    }

    private fun checkUsersLocations() {
        assertThat(usersLocationsOverlay.usersMarkers).hasSize(users.size)

        users.forEach {
            assertThat(usersLocationsOverlay.usersMarkers).containsKey(it.key)

            val marker: Marker = usersLocationsOverlay.usersMarkers[it.key]!!
            assertThat(marker.position.latitude).isEqualTo(it.value.location?.latitude)
            assertThat(marker.position.longitude).isEqualTo(it.value.location?.longitude)
        }
    }

    private fun updateTestUsers(): MutableMap<String, User> {
        val newUsers : MutableMap<String, User> = HashMap()

        users.forEach {
            val user = it.value
            val newUser = User(user.id, user.email, user.password, user.username, user.role,
                                Location(TEST_LONGITUDE, TEST_LATITUDE), Date())

            newUsers[it.key] = newUser
        }

        users = newUsers
        return newUsers
    }

    private fun createTestMarkersFromTestUsers() : MutableMap<String, Marker> {
        val usersMarkers : MutableMap<String, Marker> = HashMap()

        users.forEach {
            val marker: Marker = Marker(mapView)
            val location: Location = it.value.location!!
            marker.position = GeoPoint(location.latitude, location.longitude)
            usersMarkers[it.key] = marker
        }

        return usersMarkers
    }

    private fun removeOneOfTestUsers() {
        val userIdToRemove: String = users.keys.random()
        users.remove(userIdToRemove)
    }
}