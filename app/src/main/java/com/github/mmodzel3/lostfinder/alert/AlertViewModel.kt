package com.github.mmodzel3.lostfinder.alert

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mmodzel3.lostfinder.server.ServerCachedViewModelAbstract
import com.github.mmodzel3.lostfinder.server.ServerResponse

class AlertViewModel (private val alertRepository: AlertRepository)
    : ServerCachedViewModelAbstract<Alert>(alertRepository) {
    val alerts: MutableLiveData<MutableMap<String, Alert>>
        get() = data

    override fun runUpdates() {
        runSingleUpdate { fetchAllData() }
    }

    fun addAlert(userAlert: UserAlert): LiveData<ServerResponse> {
        return convertServerRequestToLiveData { alertRepository.addAlert(userAlert) }
    }

    fun endAlert(alertId: String): LiveData<ServerResponse> {
        return convertServerRequestToLiveData { alertRepository.endAlert(alertId) }
    }

    internal suspend fun fetchAllData(): List<Alert> {
        return alertRepository.getAllActiveAlerts()
    }
}