package com.github.mmodzel3.lostfinder.notification

interface PushNotificationListener {
    fun onNotificationReceive(notification: PushNotification)
}