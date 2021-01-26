package com.github.mmodzel3.lostfinder.server

import androidx.lifecycle.MediatorLiveData
import com.github.mmodzel3.lostfinder.notification.PushNotificationConverterAbstract

abstract class ServerPushRepositoryAbstract<T: ServerEndpointData> : ServerCachedRepositoryAbstract<T>() {
    override val data: MediatorLiveData<MutableMap<String, T>> = MediatorLiveData()

    protected fun listenToPushNotifications(pushNotificationConverter: PushNotificationConverterAbstract<T>,
                                            onReceive: (T) -> Unit) {
        data.addSource(pushNotificationConverter.lastNotification) {
            onReceive(it)
        }
    }
}