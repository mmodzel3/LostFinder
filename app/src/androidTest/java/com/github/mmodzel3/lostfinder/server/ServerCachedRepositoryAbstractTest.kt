package com.github.mmodzel3.lostfinder.server

import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ServerCachedRepositoryAbstractTest {
    companion object {
        const val MINUTE_IN_MILLISECONDS = 60 * 1000
        const val DAY_BEFORE_IN_MILLISECONDS = 24 * 60 * 60 * 1000
    }

    private lateinit var serverRepositoryAbstract: ServerCachedRepositoryImpl
    private lateinit var testData: MutableList<ServerEndpointDataImpl>

    @Before
    fun setUp() {
        serverRepositoryAbstract = ServerCachedRepositoryImpl()

        createTestData()
    }

    @Test
    fun whenUpdateDataAndHasNoDataCachedThenGotItAdded() {
        serverRepositoryAbstract.update(testData)

        assertThat(serverRepositoryAbstract.dataCache).hasSize(testData.size)

        val ids: List<String> = testData.map { it.id }
        serverRepositoryAbstract.dataCache.forEach {
            assertThat(ids).contains(it.key)
            assertThat(ids).contains(it.value.id)
        }
    }

    @Test
    fun whenUpdateDataAndHasDataCachedThenGotItUpdated() {
        addTestDataToCache()
        serverRepositoryAbstract.update(updateTestData())

        assertThat(serverRepositoryAbstract.dataCache).hasSize(testData.size)

        val ids: List<String> = testData.map { it.id }
        val minuteAfterYesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS + MINUTE_IN_MILLISECONDS)

        serverRepositoryAbstract.dataCache.forEach {
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

        serverRepositoryAbstract.update(oldTestData)
        assertThat(serverRepositoryAbstract.dataCache).hasSize(testData.size)

        val ids: List<String> = testData.map { it.id }
        val minuteAfterYesterday = Date(System.currentTimeMillis() - DAY_BEFORE_IN_MILLISECONDS + MINUTE_IN_MILLISECONDS)

        serverRepositoryAbstract.dataCache.forEach {
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
        serverRepositoryAbstract.dataCache.putAll(testDataToMap())
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