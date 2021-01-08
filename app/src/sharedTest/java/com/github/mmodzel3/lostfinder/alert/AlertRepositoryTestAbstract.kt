package com.github.mmodzel3.lostfinder.alert

import org.junit.After
import org.junit.Before

abstract class AlertRepositoryTestAbstract : AlertEndpointTestAbstract() {
    protected lateinit var alertRepository: AlertRepository

    @Before
    override fun setUp() {
        super.setUp()

        alertRepository = AlertRepository.getInstance(null)
    }

    @After
    override fun tearDown() {
        super.tearDown()

        AlertRepository.clear()
    }
}