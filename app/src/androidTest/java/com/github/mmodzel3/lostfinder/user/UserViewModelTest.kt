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

class UserViewModelTest: UserRepositoryTestAbstract() {
    companion object {
        const val MINUTE_IN_MILLISECONDS = 60 * 1000
    }

    private lateinit var userViewModel: UserViewModel
    private lateinit var latch: CountDownLatch

    @Before
    override fun setUp() {
        super.setUp()

        userViewModel = UserViewModel(userRepository)
        latch = CountDownLatch(1)

        runBlocking(Dispatchers.Main) {
            userViewModel.data.value = HashMap()
        }
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
                userViewModel.updateTask { userViewModel.fetchAllData() }
            }
        }

        assertThat(userViewModel.status.value).isEqualTo(ServerEndpointStatus.ERROR)
    }

    @Test
    fun whenFetchAllDataAndHasNothingCachedThenGotDataAdded() {
        mockGetAllUsersResponse()

        observeAndWaitForStatusChange {
            runBlocking(Dispatchers.IO) {
                userViewModel.updateTask { userViewModel.fetchAllData() }
            }
        }

        assertThat(userViewModel.data.value).hasSize(users.size)

        val usersIds = users.map { it.id }
        userViewModel.data.value!!.forEach {
            assertThat(usersIds).contains(it.key)
            assertThat(usersIds).contains(it.value.id)
        }
    }

    @Test
    fun whenFetchAllDataAndHadOldDataThenGotDataUpdated() {
        val usersMap: MutableMap<String, User> = changeTestUsersToMap()
        val yesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS + MINUTE_IN_MILLISECONDS)

        userViewModel.data.value!!.putAll(usersMap)
        updateTestUsers()
        mockGetAllUsersResponse()

        observeAndWaitForStatusChange {
            runBlocking(Dispatchers.IO) {
                userViewModel.updateTask { userViewModel.fetchAllData() }
            }
        }

        assertThat(userViewModel.data.value).hasSize(users.size)

        val usersIds = users.map { it.id }
        userViewModel.users.value?.forEach {
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
            userViewModel.status.observeForever(observer)
        }

        doAfterObserving()
        latch.await(2000, TimeUnit.MILLISECONDS)

        runBlocking(Dispatchers.Main) {
            userViewModel.status.removeObserver(observer)
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