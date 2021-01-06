package com.github.mmodzel3.lostfinder.alert

import androidx.lifecycle.MutableLiveData
import com.github.mmodzel3.lostfinder.notification.PushNotificationAlertConverter
import com.github.mmodzel3.lostfinder.security.authentication.token.InvalidTokenException
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus
import com.github.mmodzel3.lostfinder.server.ServerPushEndpointViewModelAbstract

class AlertEndpointViewModel (private val alertEndpoint: AlertEndpoint) : ServerPushEndpointViewModelAbstract<Alert>() {
    val alerts: MutableLiveData<MutableMap<String, Alert>>
        get() = data

    private val pushNotificationAlertConverter: PushNotificationAlertConverter = PushNotificationAlertConverter.getInstance()

    init {
        listenToAlertNotifications()
    }

    override fun observeUpdates() {
        runUpdate { fetchAllData() }
    }

    override fun unObserveUpdates() {
        stopUpdates()
    }

    override fun updateCache(dataToUpdate: List<Alert>): Boolean {
        val dataChanged: Boolean = super.updateCache(dataToUpdate)

        if (dataChanged) {
            removeEndedAlerts()
        }

        return dataChanged
    }

    internal suspend fun fetchAllData(): List<Alert> {
        return alertEndpoint.getAllActiveAlerts()
    }

    private fun removeEndedAlerts() {
        val idsToRemove: MutableList<String> = ArrayList()
        dataCache.forEach {
            if (it.value.endDate != null) {
                idsToRemove.add(it.key)
            }
        }

        idsToRemove.forEach {
            dataCache.remove(it)
        }
    }

    private fun listenToAlertNotifications() {
        listenToPushNotifications(pushNotificationAlertConverter) {
            update(listOf(it))
        }
    }
}