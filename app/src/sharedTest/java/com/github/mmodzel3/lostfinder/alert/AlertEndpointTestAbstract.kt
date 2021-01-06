package com.github.mmodzel3.lostfinder.alert

import com.github.mmodzel3.lostfinder.location.Location
import com.github.mmodzel3.lostfinder.server.ServerEndpointTestAbstract
import com.github.mmodzel3.lostfinder.server.ServerResponse
import com.github.mmodzel3.lostfinder.user.User
import com.github.mmodzel3.lostfinder.user.UserEndpointTestAbstract
import com.github.mmodzel3.lostfinder.user.UserRole
import org.junit.Before
import java.util.*
import kotlin.collections.ArrayList

abstract class AlertEndpointTestAbstract : ServerEndpointTestAbstract() {
    companion object {
        const val ALERT_USER_ID = "123456"
        const val ALERT_USER_NAME = "example"
        const val ALERT_USER_EMAIL = "example@example.com"
        val ALERT_USER_ROLE = UserRole.OWNER
        val ALERT_TYPE = AlertType.HELP
        const val ALERT_TITLE = "title"
        const val ALERT_DESCRIPTION = "description"
        const val ALERT_LONGITUDE = 22.1
        const val ALERT_LATITUDE = 19.2
        const val ALERT_RANGE = 20.0
        const val DAY_BEFORE_IN_MILLISECONDS = 24*60*60*1000
    }

    protected lateinit var alertEndpoint: AlertEndpoint
    protected lateinit var alerts: MutableList<Alert>
    protected lateinit var user: User

    @Before
    override fun setUp() {
        super.setUp()
        createTestUser()
        createTestAlerts()

        alertEndpoint = AlertEndpointFactory.createAlertEndpoint(null)
    }

    fun mockGetActiveAlertsResponse() {
        mockServerJsonResponse(alerts)
    }

    fun mockAddAlertResponse() {
        mockServerJsonResponse(ServerResponse.OK)
    }

    fun mockAddAlertInvalidPermissionResponse() {
        mockServerJsonResponse(ServerResponse.INVALID_PERMISSION)
    }

    fun mockEndAlertResponse() {
        mockServerJsonResponse(ServerResponse.OK)
    }

    fun mockEndAlertInvalidPermissionResponse() {
        mockServerJsonResponse(ServerResponse.INVALID_PERMISSION)
    }

    fun mockEndAlertNotFoundResponse() {
        mockServerJsonResponse(ServerResponse.NOT_FOUND)
    }

    protected fun createTestAlerts() {
        alerts = ArrayList()

        val yesterday = Date(System.currentTimeMillis() - UserEndpointTestAbstract.DAY_BEFORE_IN_MILLISECONDS)

        for (id in 1..4) {
            alerts.add(Alert(id.toString(), ALERT_TYPE, user, Location(ALERT_LATITUDE, ALERT_LONGITUDE), ALERT_RANGE.toDouble(),
                            ALERT_DESCRIPTION, yesterday, yesterday, null, yesterday))
        }
    }

    protected fun createTestUser() {
        user = User(ALERT_USER_ID, ALERT_USER_EMAIL, null,
            ALERT_USER_NAME, ALERT_USER_ROLE, null, Date(), Date(), false, false, null)
    }
}