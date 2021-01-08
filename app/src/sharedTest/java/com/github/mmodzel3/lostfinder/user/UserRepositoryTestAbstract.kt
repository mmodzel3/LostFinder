package com.github.mmodzel3.lostfinder.user

import org.junit.After
import org.junit.Before

abstract class UserRepositoryTestAbstract : UserEndpointTestAbstract() {
    protected lateinit var userRepository: UserRepository

    @Before
    override fun setUp() {
        super.setUp()

        userRepository = UserRepository.getInstance(null)
    }

    @After
    override fun tearDown() {
        super.tearDown()

        UserRepository.clear()
    }
}