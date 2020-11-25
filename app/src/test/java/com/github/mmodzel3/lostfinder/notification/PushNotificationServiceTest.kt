package com.github.mmodzel3.lostfinder.notification

import com.google.common.truth.Truth.assertThat
import com.google.firebase.messaging.RemoteMessage
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class PushNotificationServiceTest {
    private lateinit var pushNotificationService: PushNotificationService
    private lateinit var pushNotification: PushNotification

    @Before
    fun setUp() {
        pushNotificationService = PushNotificationService()
    }

    @Test
    fun whenRegisterListenerThenItIsAdded() {
        val listener: PushNotificationListener = createPushNotificationListener()

        pushNotificationService.registerListener(listener)

        assertThat(pushNotificationService.notificationListeners).contains(listener)
    }

    @Test
    fun whenUnregisterListenerThenItIsRemoved() {
        val listener: PushNotificationListener = createPushNotificationListener()
        pushNotificationService.registerListener(listener)
        pushNotificationService.unregisterListener(listener)

        assertThat(pushNotificationService.notificationListeners).doesNotContain(listener)
    }

    private fun createPushNotificationListener(): PushNotificationListener {
        return object : PushNotificationListener {
            override fun onNotificationReceive(notification: PushNotification) {
                pushNotification = notification
            }
        }
    }
}