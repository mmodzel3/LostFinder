package com.github.mmodzel3.lostfinder.alert

import androidx.lifecycle.MutableLiveData
import com.github.mmodzel3.lostfinder.notification.PushNotificationAlertConverter
import com.github.mmodzel3.lostfinder.security.authentication.token.TokenManager
import com.github.mmodzel3.lostfinder.server.ServerPushRepositoryAbstract
import com.github.mmodzel3.lostfinder.server.ServerResponse

class AlertRepository private constructor (private val tokenManager: TokenManager?) : ServerPushRepositoryAbstract<Alert>() {
    companion object {
        private var alertRepository: AlertRepository? = null

        fun getInstance(tokenManager: TokenManager?): AlertRepository {
            if (alertRepository == null) {
                alertRepository = AlertRepository(tokenManager)
            }

            return alertRepository!!
        }

        fun clear() {
            alertRepository = null
        }
    }

    val alerts: MutableLiveData<MutableMap<String, Alert>>
        get() = data

    private val pushNotificationAlertConverter: PushNotificationAlertConverter = PushNotificationAlertConverter.getInstance()

    private val alertEndpoint: AlertEndpoint by lazy {
        AlertEndpointFactory.createAlertEndpoint(tokenManager)
    }

    init {
        listenToAlertNotifications()
    }

    suspend fun getAllActiveAlerts(): List<Alert> {
        return fetchAndUpdate { alertEndpoint.getAllActiveAlerts() }
    }

    suspend fun addAlert(userAlert: UserAlert): ServerResponse {
        return alertEndpoint.addAlert(userAlert)
    }

    suspend fun endAlert(alertId: String): ServerResponse {
        return alertEndpoint.endAlert(alertId)
    }

    override fun updateCache(dataToUpdate: List<Alert>): Boolean {
        val dataChanged: Boolean = super.updateCache(dataToUpdate)

        if (dataChanged) {
            removeEndedAlerts()
        }

        return dataChanged
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