package com.github.mmodzel3.lostfinder.user

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

class UserEndpointViewModelTest: UserEndpointTestAbstract() {
    companion object {
        const val MINUTE_IN_MILLISECONDS = 60 * 1000
    }

    private lateinit var userEndpointViewModel: UserEndpointViewModel
    private lateinit var latch: CountDownLatch

    @Before
    override fun setUp() {
        super.setUp()

        userEndpointViewModel = UserEndpointViewModel(userEndpoint)
        latch = CountDownLatch(1)
    }

    @After
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun whenFetchAllDataAndUserApiAccessErrorThenStatusIsError() {
        mockServerFailureResponse()

        observeAndWaitForStatusChange {
            runBlocking(Dispatchers.IO) {
                userEndpointViewModel.updateTask { userEndpointViewModel.fetchAllData() }
            }
        }

        assertThat(userEndpointViewModel.status.value).isEqualTo(ServerEndpointStatus.ERROR)
    }

    @Test
    fun whenFetchAllDataAndHasNothingCachedThenGotDataAdded() {
        mockGetAllUsersResponse()

        observeAndWaitForStatusChange {
            runBlocking(Dispatchers.IO) {
                userEndpointViewModel.updateTask { userEndpointViewModel.fetchAllData() }
            }
        }

        assertThat(userEndpointViewModel.dataCache).hasSize(users.size)

        val usersIds = users.map { it.id }
        userEndpointViewModel.dataCache.forEach {
            assertThat(usersIds).contains(it.key)
            assertThat(usersIds).contains(it.value.id)
        }
    }

    @Test
    fun whenFetchAllDataAndHadOldDataThenGotDataUpdated() {
        val usersMap: MutableMap<String, User> = changeTestUsersToMap()
        val yesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS + MINUTE_IN_MILLISECONDS)

        userEndpointViewModel.dataCache.putAll(usersMap)
        updateTestUsers()
        mockGetAllUsersResponse()

        observeAndWaitForStatusChange {
            runBlocking(Dispatchers.IO) {
                userEndpointViewModel.updateTask { userEndpointViewModel.fetchAllData() }
            }
        }

        assertThat(userEndpointViewModel.dataCache).hasSize(users.size)

        val usersIds = users.map { it.id }
        userEndpointViewModel.users.value?.forEach {
            assertThat(usersIds).contains(it.key)
            assertThat(usersIds).contains(it.value.id)
            assertThat(it.value.lastUpdateDate).isAtLeast(yesterday)
        }
    }

    private fun observeAndWaitForStatusChange(doAfterObserving: () -> Unit) {
        val observer = Observer<ServerEndpointStatus> {
            latch.countDown()
        }

        runBlocking(Dispatchers.Main) {
            userEndpointViewModel.status.observeForever(observer)
        }

        doAfterObserving()
        latch.await(2000, TimeUnit.MILLISECONDS)

        runBlocking(Dispatchers.Main) {
            userEndpointViewModel.status.removeObserver(observer)
        }
    }

    private fun changeTestUsersToMap() : MutableMap<String, User> {
        val map : MutableMap<String, User> = HashMap()
        users.forEach { map[it.id] = it }

        return map
    }

    private fun updateTestUsers() {
        val newUsers: MutableList<User> = ArrayList()

        users.forEach {
            newUsers.add(User(it.id, it.email, it.password, it.username, it.role, it.location, Date(), Date(), false, false, null))
        }

        users = newUsers
    }
}