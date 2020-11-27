package com.github.mmodzel3.lostfinder.server

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ServerEndpointViewModelAbstractTest {
    companion object {
        const val MINUTE_IN_MILLISECONDS = 60 * 1000
        const val DAY_BEFORE_IN_MILLISECONDS = 24 * 60 * 60 * 1000
    }

    private lateinit var serverEndpointViewModelAbstract: ServerEndpointViewModelImpl
    private lateinit var testData: MutableList<ServerEndpointDataImpl>

    @Before
    fun setUp() {
        serverEndpointViewModelAbstract = ServerEndpointViewModelImpl()

        createTestData()
    }

    @Test
    fun whenUpdateDataAndHasNoDataCachedThenGotItAdded() {
        serverEndpointViewModelAbstract.update(testData)

        assertThat(serverEndpointViewModelAbstract.dataCache).hasSize(testData.size)

        val ids: List<String> = testData.map { it.id }
        serverEndpointViewModelAbstract.dataCache.forEach {
            assertThat(ids).contains(it.key)
            assertThat(ids).contains(it.value.id)
        }
    }

    @Test
    fun whenUpdateDataAndHasDataCachedThenGotItUpdated() {
        addTestDataToCache()
        serverEndpointViewModelAbstract.update(updateTestData())

        assertThat(serverEndpointViewModelAbstract.dataCache).hasSize(testData.size)

        val ids: List<String> = testData.map { it.id }
        val minuteAfterYesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS + MINUTE_IN_MILLISECONDS)

        serverEndpointViewModelAbstract.dataCache.forEach {
            assertThat(ids).contains(it.key)
            assertThat(ids).contains(it.value.id)
            assertThat(it.value.lastUpdateDate).isAtLeast(minuteAfterYesterday)
        }
    }

    @Test
    fun whenUpdateDataWithOldDataAndHasNewDataCachedThenItIsNotUpdated() {
        val oldTestData: List<ServerEndpointDataImpl> = testData

        updateTestData()
        addTestDataToCache()

        serverEndpointViewModelAbstract.update(oldTestData)
        assertThat(serverEndpointViewModelAbstract.dataCache).hasSize(testData.size)

        val ids: List<String> = testData.map { it.id }
        val minuteAfterYesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS + MINUTE_IN_MILLISECONDS)

        serverEndpointViewModelAbstract.dataCache.forEach {
            assertThat(ids).contains(it.key)
            assertThat(ids).contains(it.value.id)
            assertThat(it.value.lastUpdateDate).isAtLeast(minuteAfterYesterday)
        }
    }

    private fun createTestData() {
        testData = ArrayList()
        var id: Int = 0
        val yesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS)

        testData.add(ServerEndpointDataImpl((id++).toString(), yesterday))
        testData.add(ServerEndpointDataImpl((id++).toString(), yesterday))
        testData.add(ServerEndpointDataImpl((id++).toString(), yesterday))
        testData.add(ServerEndpointDataImpl((id).toString(), yesterday))
    }

    private fun testDataToMap() : Map<String, ServerEndpointDataImpl> {
        val testDataMap: MutableMap<String, ServerEndpointDataImpl> = HashMap()

        testData.forEach {
            testDataMap[it.id] = it
        }

        return testDataMap
    }

    private fun addTestDataToCache() {
        serverEndpointViewModelAbstract.dataCache.putAll(testDataToMap())
    }

    private fun updateTestData() : List<ServerEndpointDataImpl> {
        val newTestData: MutableList<ServerEndpointDataImpl> = ArrayList()

        testData.forEach {
            newTestData.add(ServerEndpointDataImpl(it.id, Date()))
        }

        testData = newTestData
        return newTestData
    }
}