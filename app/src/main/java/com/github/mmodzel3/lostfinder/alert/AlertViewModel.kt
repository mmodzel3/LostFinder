package com.github.mmodzel3.lostfinder.alert

import androidx.lifecycle.MutableLiveData
import com.github.mmodzel3.lostfinder.server.ServerResponse
import com.github.mmodzel3.lostfinder.server.ServerViewModelAbstract

class AlertViewModel (private val alertRepository: AlertRepository)
    : ServerViewModelAbstract<Alert>(alertRepository) {
    val alerts: MutableLiveData<MutableMap<String, Alert>>
        get() = data

    override fun runUpdates() {
        runSingleUpdate { fetchAllData() }
    }

    suspend fun addAlert(userAlert: UserAlert): ServerResponse {
        return alertRepository.addAlert(userAlert)
    }

    suspend fun endAlert(alertId: String): ServerResponse {
        return alertRepository.endAlert(alertId)
    }

    internal suspend fun fetchAllData(): List<Alert> {
        return alertRepository.getAllActiveAlerts()
    }
}