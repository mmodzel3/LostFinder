package com.github.mmodzel3.lostfinder.alert

import androidx.lifecycle.Observer
import com.github.mmodzel3.lostfinder.server.ServerEndpointStatus
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class AlertViewModelTest: AlertRepositoryTestAbstract() {
    companion object {
        const val MINUTE_IN_MILLISECONDS = 60 * 1000
    }

    private lateinit var alertViewModel: AlertViewModel
    private lateinit var latch: CountDownLatch

    @Before
    override fun setUp() {
        super.setUp()

        alertViewModel = AlertViewModel(alertRepository)
        latch = CountDownLatch(1)

        runBlocking(Dispatchers.Main) {
            alertViewModel.data.value = HashMap()
        }
    }

    @After
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun whenUpdateDataAndAlertApiAccessErrorThenStatusIsError() {
        mockServerFailureResponse()

        observeAndWaitForStatusChange {
            runBlocking(Dispatchers.IO) {
                alertViewModel.updateTask { alertViewModel.fetchAllData() }
            }
        }

        assertThat(alertViewModel.status.value).isEqualTo(ServerEndpointStatus.ERROR)
    }

    @Test
    fun whenUpdateDataAndNoDataThenGotDataAdded() {
        mockGetActiveAlertsResponse()

        observeAndWaitForStatusChange {
            runBlocking(Dispatchers.IO) {
                alertViewModel.updateTask { alertViewModel.fetchAllData() }
            }
        }

        assertThat(alertViewModel.data.value).hasSize(alerts.size)

        val alertsIds = alerts.map { it.id }
        alertViewModel.data.value!!.forEach {
            assertThat(alertsIds).contains(it.key)
            assertThat(alertsIds).contains(it.value.id)
        }
    }

    @Test
    fun whenUpdateDataAndHasDataCachedThenGotDataUpdated() {
        alertViewModel.data.value!!.putAll(changeTestAlertsToMap())
        updateTestAlerts()

        mockGetActiveAlertsResponse()

        observeAndWaitForStatusChange {
            runBlocking(Dispatchers.IO) {
                alertViewModel.updateTask { alertViewModel.fetchAllData() }
            }
        }

        assertThat(alertViewModel.data.value).hasSize(alerts.size)

        val yesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS + MINUTE_IN_MILLISECONDS)
        val alertsIds = alerts.map { it.id }
        alertViewModel.data.value!!.forEach {
            assertThat(alertsIds).contains(it.key)
            assertThat(alertsIds).contains(it.value.id)
            assertThat(it.value.lastUpdateDate).isAtLeast(yesterday)
        }
    }

    private fun observeAndWaitForStatusChange(doAfterObserving: () -> Unit) {
        val observer = Observer<ServerEndpointStatus> {
            latch.countDown()
        }

        runBlocking(Dispatchers.Main) {
            alertViewModel.status.observeForever(observer)
        }

        doAfterObserving()
        latch.await(2000, TimeUnit.MILLISECONDS)

        runBlocking(Dispatchers.Main) {
            alertViewModel.status.removeObserver(observer)
        }
    }

    private fun changeTestAlertsToMap() : MutableMap<String, Alert> {
        val map : MutableMap<String, Alert> = HashMap()
        alerts.forEach { map[it.id] = it }

        return map
    }

    private fun updateTestAlerts(): MutableList<Alert> {
        val newAlerts: MutableList<Alert> = ArrayList()

        alerts.forEach {
            newAlerts.add(Alert(it.id, it.type, it.user, it.location, it.range,
                            it.description, it.sendDate, it.receivedDate, it.endDate, Date()
            ))
        }

        alerts = newAlerts
        return newAlerts
    }
}